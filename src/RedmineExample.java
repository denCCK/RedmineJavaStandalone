import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.RedmineManagerFactory;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.Project;
import com.taskadapter.redmineapi.bean.User;

import java.util.List;
import java.util.Scanner;

public class RedmineExample {

    private static List<String> statusList = List.of("Новая", "В работе", "Решена", "Нужен отклик", "Закрыта", "Отклонена");
    private List<String> priorityList = List.of("Низкий", "Нормальный", "Высокий", "Срочный", "Немедленный");

    public static String issueToString(Issue issue) {
        return "\nЗадача - " + issue.getSubject() + "\nВерсия - " + issue.getTargetVersion().getName() +
                "\nОписание:\n" + issue.getDescription() + "\nCтатус - " + issue.getStatusName() +
                "\nПриоритет - " + issue.getPriorityText() + "\nСтепень выполнения - " + issue.getDoneRatio() + "%\n";
    }
    public static void main(String[] args) {

        String url = "http://localhost:3000/";
        String apiKey = "ed56aca380ba026fe7d794d105444dbf1f2a3e42";
        RedmineManager mgr = RedmineManagerFactory.createWithApiKey(url, apiKey);
        Scanner in = new Scanner(System.in);
        Boolean isExit = false;

        try {
            System.out.println("Redmine-standalone");
            //Вывод списка проектов
            List<Project> projects = mgr.getProjectManager().getProjects();
            List<User> users = mgr.getUserManager().getUsers();

            while (!isExit) {

                System.out.println("Параметры выбора задач: 1 - По проектам | 2 - По конкретным пользователям | 0 - Выход");
                int sortOption = Integer.parseInt(in.nextLine());
                switch (sortOption) {
                    case 1 -> {
                        System.out.println("Список проектов");
                        for (Project project : projects) {
                            System.out.print(project.getName() + "\t");
                        }
                        System.out.println("\n");

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
                            System.out.println("Опции: 1 - Все задачи | 2 - Степень выполения более 50% | 3 - Степень выполнения менее 50% | 4 - По статусу (По умолчанию в работе) | 0 - Выход");
                            int issueOption = Integer.parseInt(in.nextLine());
                            String selectedStatus = "В работе";
                            if (issueOption == 4)
                            {
                                for (String status : statusList)
                                    System.out.println(status + "\t");
                                System.out.println("Выберите статус");
                                selectedStatus = in.nextLine();
                                if (!statusList.contains(selectedStatus)) {
                                    selectedStatus = "В работе";
                                    System.out.println("Неверно выбранный статус, выставлено значение по умолчанию");
                                }
                            }
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
