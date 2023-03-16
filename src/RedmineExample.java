import com.taskadapter.redmineapi.Params;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.RedmineManagerFactory;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.Project;
import com.taskadapter.redmineapi.bean.User;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class RedmineExample {

    private static List<String> statusList = List.of("Новая", "В работе", "Решена", "Нужен отклик", "Закрыта", "Отклонена");
    //private static Map<String, Integer> statusMap = Map.of("Новая", 1, "В работе", 2, "Решена", 3, "Нужен отклик", 4, "Закрыта", 5, "Отклонена", 6);
    private static List<String> priorityList = List.of("Низкий", "Нормальный", "Высокий", "Срочный", "Немедленный");
    //private static Map<String, Integer> priorityMap = Map.of("Низкий", 1, "Нормальный", 2, "Высокий", 3, "Срочный", 4, "Закрыта", 5, "Отклонена", 6);

    public static String issueToString(Issue issue) {
        return "\nЗадача - " + issue.getSubject() + "\nВерсия - " + issue.getTargetVersion().getName() +
                "\nОписание:\n" + issue.getDescription() + "\nCтатус - " + issue.getStatusName() +
                "\nПриоритет - " + issue.getPriorityText() + "\nСтепень выполнения - " + issue.getDoneRatio() + "%\n";
    }

    public static void IssuesPrint (List<Issue> issues) {
        for (Issue issue : issues) {
            System.out.println(issueToString(issue));
        }
        System.out.println("\n");
    }

    public static void ProjectPrint (List<Project> projects) {
        for (Project project : projects) {
            System.out.print(project.getName() + "(" + project.getId() + ")" + "\t");
        }
        System.out.println("\n");
    }

    public static void SearchAllIssues(RedmineManager mgr, Integer project_id) {
        try {
            Params params = new Params()
                    .add("project_id", Integer.toString(project_id))
                    .add("status_id", "*");

            List<Issue> issues = mgr.getIssueManager().getIssues(params).getResults();
            IssuesPrint(issues);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void SearchByStatusIssues(RedmineManager mgr, Integer project_id, Integer status_id) {
        try {
            Params params = new Params()
                    .add("project_id", Integer.toString(project_id))
                    .add("status_id", Integer.toString(status_id));

            List<Issue> issues = mgr.getIssueManager().getIssues(params).getResults();
            IssuesPrint(issues);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void SearchByPriorityIssues(RedmineManager mgr, Integer project_id, Integer priority_id) {
        try {
            Params params = new Params()
                    .add("project_id", Integer.toString(project_id))
                    .add("status_id", "*");

            List<Issue> issues = mgr.getIssueManager().getIssues(params).getResults();
            IssuesPrint(issues);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        String url = "http://localhost:3000/";
        String apiKey = "ed56aca380ba026fe7d794d105444dbf1f2a3e42";
        RedmineManager mgr = RedmineManagerFactory.createWithApiKey(url, apiKey);
        Scanner in = new Scanner(System.in);
        Boolean isExit = false;

        try {
            System.out.println("Redmine-standalone");
            List<Project> projects = mgr.getProjectManager().getProjects();
            List<User> users = mgr.getUserManager().getUsers();

            while (!isExit) {

                System.out.println("Параметры выбора задач: 1 - По проектам | 2 - По конкретным пользователям | 0 - Выход");
                int sortOption = Integer.parseInt(in.nextLine());
                switch (sortOption) {
                    case 1 -> {
                        System.out.println("Список проектов");
                        //System.out.println(projects.toString());
                        ProjectPrint(projects);

                        System.out.println("Выберите проект");
                        String currentProjectName = in.nextLine();
                        Project currentProject = null;
                        for (Project project : projects) {
                            if (project.getName().equals(currentProjectName))
                                currentProject = project;
                        }

                        if (currentProject != null) {
                            System.out.println("Проект: " + currentProject.getName());
                            List<Issue> issues = mgr.getIssueManager().getIssues(currentProject.getIdentifier(), null);
                            System.out.println("Опции: 1 - Все задачи | 2 - Степень выполения более 50% | " +
                                    "3 - Степень выполнения менее 50% | 4 - По статусу (По умолчанию в работе) | " +
                                    "5 - По приоритету (По умолчанию нормальный) | 6 - Полный фильтр задач | " +
                                    "0 - Выход");
                            int issueOption = Integer.parseInt(in.nextLine());
                            String selectedStatus = "В работе";
                            String selectedPriority = "Нормальный";
                            if (issueOption == 4 || issueOption == 6) {
                                //System.out.println(statusList.toString());
                                for (String status : statusList)
                                    System.out.println(status + "\t");
                                System.out.println("Выберите статус задачи");
                                selectedStatus = in.nextLine();
                                if (!statusList.contains(selectedPriority)) {
                                    selectedStatus = "В работе";
                                    System.out.println("Неверно выбранный статус, выставлено значение по умолчанию");
                                }
                            }
                            if (issueOption == 5 || issueOption == 6) {
                                for (String priority : priorityList)
                                    System.out.println(priority + "\t");
                                System.out.println("Выберите приоритет задачи");
                                selectedPriority = in.nextLine();
                                if (!priorityList.contains(selectedPriority)) {
                                    selectedPriority = "Нормальный";
                                    System.out.println("Неверно выбранный приоритет задача, выставлено значение по умолчанию");
                                }
                            }
                            //SearchAllIssues(mgr, currentProject.getId());
                            for (Issue issue : issues) {
                                switch (issueOption) {
                                    case 1 -> System.out.println(issueToString(issue));
                                    case 2 -> {
                                        if (issue.getDoneRatio() > 50)
                                            System.out.println(issueToString(issue));
                                    }
                                    case 3 -> {
                                        if (issue.getDoneRatio() < 50)
                                            System.out.println(issueToString(issue));
                                    }
                                    case 4 -> {
                                        if (issue.getStatusName().equals(selectedStatus))
                                            System.out.println(issueToString(issue));
                                    }
                                    case 5 -> {
                                        if (issue.getPriorityText().equals(selectedPriority))
                                            System.out.println(issueToString(issue));
                                    }
                                    case 6 -> {
                                        if (issue.getStatusName().equals(selectedStatus) && issue.getPriorityText().equals(selectedPriority))
                                            System.out.println(issueToString(issue));
                                    }
                                    case 0 -> isExit = true;
                                    default -> {
                                        System.out.println("Некорректный ввод опции\n");
                                    }
                                }
                            }
                        }
                        else System.out.println("Некорректный ввод проекта\n");
                    }
                    case 2 -> {
                        System.out.println("Введите логин пользователя");
                        String userLogin = in.nextLine();
                        User currentUser = null;
                        for (User user : users) {
                            if (user.getLogin().equals(userLogin)) {
                                currentUser = user;
                            }
                        }

                        if (currentUser != null) {
                            for (Project project : projects) {
                                List<Issue> issues = mgr.getIssueManager().getIssues(project.getIdentifier(), null);
                                System.out.println("Проект: " + project.getName());
                                for (Issue issue : issues) {
                                    if (issue.getAssigneeId().equals(currentUser.getId()))
                                        System.out.println(issueToString(issue));
                                }
                            }
                        }
                        else System.out.println("Неверно введенный логин");
                    }
                    case 0 -> isExit = true;
                    default -> {
                        System.out.println("Некорректный ввод параметра выбора\n");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
