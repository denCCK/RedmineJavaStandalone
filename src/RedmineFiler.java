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
    private static final int WH = 8;
    private static final int[][] WORK_HOURS = {
            {0, 0, 0, 0, 0, 0, 0}, //1
            {0, WH, WH, WH, WH, WH, 0}, //2
            {0, WH, WH, WH, WH, WH, 0}, //3
            {0, WH, WH, WH, WH, WH, 0}, //4
            {0, WH, WH, WH, WH, WH, 0}, //5
            {0, WH, WH, WH, WH, WH, 0}, //6
            {0, WH, WH, WH, WH, WH, 0}, //7
            {0, WH, WH, WH - 1, 0, 0, 0}, //8
            {0, WH, WH, WH, WH, WH, 0}, //9
            {0, WH, WH - 1, 0, WH, WH, 0}, //10
            {0, WH, WH, WH, WH, WH, 0}, //11
            {0, WH, WH, WH, WH, WH, 0}, //12
            {0, WH, WH, WH, WH, WH, 0}, //13
            {0, WH, WH, WH, WH, WH, 0}, //14
            {0, WH, WH, WH, WH, WH, 0}, //15
            {0, WH, WH, WH, WH, WH, 0}, //16
            {0, WH, WH, WH, WH, WH, 0}, //17
            {0, 0, WH, WH, WH, WH, 0}, //18
            {0, 0, 0, WH, WH, WH, 0}, //19
            {0, WH, WH, WH, WH, WH, 0}, //20
            {0, WH, WH, WH, WH, WH, 0}, //21
            {0, WH, WH, WH, WH, WH, 0}, //22
            {0, WH, WH, WH, WH, WH, 0}, //23
            {0, 0, WH, WH, WH, WH, 0}, //24
            {0, WH, WH, WH, WH, WH, 0}, //25
            {0, WH, WH, WH, WH, WH, 0}, //26
            {0, WH, WH, WH, WH, WH, 0}, //27
            {0, WH, WH, WH, WH, WH, 0}, //28
            {0, WH, WH, WH, WH, WH, 0}, //29
            {0, WH, WH, WH, WH, WH, 0}, //30
            {0, WH, WH, WH, WH, WH, 0}, //31
            {0, WH, WH, WH, WH, WH, 0}, //32
            {0, WH, WH, WH, WH, WH, 0}, //33
            {0, WH, WH, WH, WH, WH, 0}, //34
            {0, WH, WH, WH, WH, WH, 0}, //35
            {0, WH, WH, WH, WH, WH, 0}, //36
            {0, WH, WH, WH, WH, WH, 0}, //37
            {0, WH, WH, WH, WH, WH, 0}, //38
            {0, WH, WH, WH, WH, WH, 0}, //39
            {0, WH, WH, WH, WH, WH, 0}, //40
            {0, WH, WH, WH, WH, WH, 0}, //41
            {0, WH, WH, WH, WH, WH, 0}, //42
            {0, WH, WH, WH, WH, WH, 0}, //43
            {0, WH, WH, WH, WH, WH - 1, 0}, //44
            {0, 0, WH, WH, WH, WH, 0}, //45
            {0, WH, WH, WH, WH, WH, 0}, //46
            {0, WH, WH, WH, WH, WH, 0}, //47
            {0, WH, WH, WH, WH, WH, 0}, //48
            {0, WH, WH, WH, WH, WH, 0}, //49
            {0, WH, WH, WH, WH, WH, 0}, //50
            {0, WH, WH, WH, WH, WH, 0}, //51
            {0, WH, WH, WH, WH, WH, 0}, //52
    };

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

    //{0,40,40,32}
    //
    //
    //
    //

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

    public List<String> SearchLess40HourDWeeks(Integer assigned_to_id, Integer year, Integer month) {
        try {
            List<String> weeks = new ArrayList<>();

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
            Map<String, Float> workHours = new HashMap<>();
            for (TimeEntry timeEntry : timeEntries) {
                workHours.put(dateFormat.format(timeEntry.getSpentOn()), timeEntry.getHours());
            }

            calendar.set(Calendar.DAY_OF_MONTH, 1);

            while (calendar.get(Calendar.MONTH) < month) {
                int weekOfYear = calendar.get(Calendar.WEEK_OF_YEAR);
                String weekString = dateFormat.format(calendar.getTime());
                String dayString = weekString;
                float hours = 0, weekWorkHours = 0;
                while (calendar.get(Calendar.WEEK_OF_YEAR) == weekOfYear) {
                    if (calendar.get(Calendar.MONTH) < month) {
                        weekWorkHours += WORK_HOURS[weekOfYear - 1][calendar.get(Calendar.DAY_OF_WEEK) - 1];
                        dayString = dateFormat.format(calendar.getTime());
                        if (workHours.containsKey(dayString))
                            hours += workHours.get(dayString);
                    }
                    calendar.add(Calendar.DAY_OF_MONTH, 1);
                }
                if (hours < weekWorkHours)
                    weeks.add(weekString + " - " + dayString);
            }
            return weeks;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
