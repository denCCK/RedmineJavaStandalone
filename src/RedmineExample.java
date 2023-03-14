import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.RedmineManagerFactory;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.Project;

import java.util.List;
import java.util.Scanner;

public class RedmineExample {

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
            System.out.println("Список проектов");
            for (Project project : projects) {
                System.out.print(project.getName() + "\t");
            }
            System.out.println("\n");

            while (!isExit) {

                //Выбор нужного проекта
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
                    System.out.println("Опции: 1 - Все задачи | 2 - Степень выполения более 50% | 3 - Степень выполнения менее 50% | 4 - Статус в работе | 0 - Выход");
                    int option = Integer.parseInt(in.nextLine());
                    for (Issue issue : issues) {
                        switch (option) {
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
                                if (issue.getStatusName().equals("В работе"))
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
