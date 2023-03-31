import com.taskadapter.redmineapi.Include;
import com.taskadapter.redmineapi.Params;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.RedmineManagerFactory;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.Project;
import com.taskadapter.redmineapi.bean.TimeEntry;
import com.taskadapter.redmineapi.bean.User;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class RedmineFiler {

    private static final String url = "http://localhost:3000/";
    private static  final String apiKey = "ed56aca380ba026fe7d794d105444dbf1f2a3e42";
    private static RedmineManager mgr;
    private static final Map<String, Integer> statusMap = Map.of("Новая", 1, "В работе", 2, "Решена", 3, "Нужен отклик", 4, "Закрыта", 5, "Отклонена", 6);
    private static final Map<String, Integer> priorityMap = Map.of("Низкий", 1, "Нормальный", 2, "Высокий", 3, "Срочный", 4, "Закрыта", 5, "Отклонена", 6);

    public RedmineFiler() {
        this.mgr = RedmineManagerFactory.createWithApiKey(url, apiKey);
    }

    public List<Project> getProjectsList() {
        try {
            return mgr.getProjectManager().getProjects();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<User> getUsersList() {
        try {
            return mgr.getUserManager().getUsers();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Integer getStatusId(String status) {
        return statusMap.get(status);
    }

    public Integer getPriorityId(String priority) {
        return priorityMap.get(priority);
    }

    public List<Issue> SearchAllIssues(Integer project_id) {
        try {
            Params params = new Params()
                    .add("project_id", Integer.toString(project_id))
                    .add("status_id", "*");

            return mgr.getIssueManager().getIssues(params).getResults();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Issue> SearchByStatusIssues(Integer project_id, Integer status_id) {
        try {
            Params params = new Params()
                    .add("project_id", Integer.toString(project_id))
                    .add("status_id", Integer.toString(status_id));

            return mgr.getIssueManager().getIssues(params).getResults();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Issue> SearchByPriorityIssues(Integer project_id, Integer priority_id) {
        try {
            Params params = new Params()
                    .add("project_id", Integer.toString(project_id))
                    .add("priority_id", Integer.toString(priority_id));

            return mgr.getIssueManager().getIssues(params).getResults();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Issue> SearchByLessHalfDoneRatioIssues(Integer project_id) {
        try {
            Params params = new Params()
                    .add("project_id", Integer.toString(project_id))
                    .add("done_ratio", ">=" + 0)
                    .add("done_ratio", "<=" + 50);

            return mgr.getIssueManager().getIssues(params).getResults();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Issue> SearchByMoreHalfDoneRatioIssues(Integer project_id) {
        try {
            Params params = new Params()
                    .add("project_id", Integer.toString(project_id))
                    .add("done_ratio", ">=" + 50)
                    .add("done_ratio", "<=" + 100);

            return mgr.getIssueManager().getIssues(params).getResults();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Issue> SearchAllAssignedIssues(Integer assigned_to_id) {
        try {
            Params params = new Params()
                    .add("assigned_to_id", Integer.toString(assigned_to_id));

            return mgr.getIssueManager().getIssues(params).getResults();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<TimeEntry> SearchByAssignedAndDateIssues(Integer assigned_to_id, String fromDate , String toDate) {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("user_id", Integer.toString(assigned_to_id));
            params.put("from", fromDate);
            params.put("to", toDate);
            return mgr.getTimeEntryManager().getTimeEntries(params).getResults();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Map<String, Float> filterByValueLessThan(Map<String, Float> map, float threshold) {
        return map.entrySet().stream()
                .filter(entry -> Float.compare(entry.getValue(), threshold) < 0)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public Map<String, Float> SearchLess8HourDays(Integer assigned_to_id, String fromDate , String toDate) {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("user_id", Integer.toString(assigned_to_id));
            params.put("from", fromDate);
            params.put("to", toDate);
            List<TimeEntry> timeEntries = mgr.getTimeEntryManager().getTimeEntries(params).getResults();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
            Map<String, Float> days = new HashMap<>();
            timeEntries.forEach(n -> {
                        if (days.containsKey(dateFormat.format(n.getSpentOn()))) {
                            String day = dateFormat.format(n.getSpentOn());
                            Float hours = days.get(day) + n.getHours();
                            days.replace(day, hours);
                        }
                        else {
                            days.put(dateFormat.format(n.getSpentOn()), n.getHours());
                        }
                    });
            return filterByValueLessThan(days, 8);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Map<String, Float> SearchLess40HourDWeeks(Integer assigned_to_id, Integer year, Integer month) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month - 1);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            String fromDate = dateFormat.format(calendar.getTime());

            int lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            calendar.set(Calendar.DAY_OF_MONTH, lastDay);
            String toDate = dateFormat.format(calendar.getTime());

            Map<String, String> params = new HashMap<>();
            params.put("user_id", Integer.toString(assigned_to_id));
            params.put("from", fromDate);
            params.put("to", toDate);
            List<TimeEntry> timeEntries = mgr.getTimeEntryManager().getTimeEntries(params).getResults();

            calendar.set(Calendar.DAY_OF_MONTH, 1);

            while (calendar.get(Calendar.DAY_OF_MONTH) <= lastDay) {
                int weekOfYear = calendar.get(Calendar.WEEK_OF_YEAR);

                System.out.println("Неделя " + weekOfYear + ":");

                for (int dayOfWeek = Calendar.SUNDAY; dayOfWeek <= Calendar.SATURDAY; dayOfWeek++) {
                    Calendar day = (Calendar) calendar.clone();
                    day.set(Calendar.DAY_OF_WEEK, dayOfWeek);

                    if (day.get(Calendar.MONTH) == calendar.get(Calendar.MONTH)) {
                        System.out.println(dateFormat.format(day.getTime()));
                    }
                }

                calendar.add(Calendar.WEEK_OF_MONTH, 1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
