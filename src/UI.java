import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.Project;
import com.taskadapter.redmineapi.bean.TimeEntry;
import com.taskadapter.redmineapi.bean.User;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class UI {

    private static final RedmineFiler redmineFiler = new RedmineFiler();
    private static final Scanner in = new Scanner(System.in);

    private static final List<String> statusList = List.of("Новая", "В работе", "Решена", "Нужен отклик", "Закрыта", "Отклонена");
    private static final List<String> priorityList = List.of("Низкий", "Нормальный", "Высокий", "Срочный", "Немедленный");

    public static String issueToString(Issue issue) {
        return "\nЗадача - " + issue.getSubject() + "\nВерсия - " + issue.getTargetVersion().getName() +
                "\nОписание:\n" + issue.getDescription() + "\nCтатус - " + issue.getStatusName() +
                "\nПриоритет - " + issue.getPriorityText() + "\nСтепень выполнения - " + issue.getDoneRatio() + "%\n";
    }

    public static String timeEntryToString(TimeEntry timeEntry) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return "\nПользователь - " + timeEntry.getUserName() + "\nПроект - " + timeEntry.getProjectName() +
                "\nПотрачено: " + timeEntry.getHours() + "\nДата - " + dateFormat.format(timeEntry.getSpentOn()) + "\n";
    }

//    public static <T> void ListPrint(List<T> list) {
//        for (T element : list) {
//            System.out.println(element.toString());
//        }
//        System.out.println("\n");
//    }

    public static void IssuesPrint(List<Issue> list) {
        for (Issue issue : list) {
            System.out.println(issueToString(issue));
        }
        System.out.println("\n");
    }

    public static void TimeEntriesPrint(List<TimeEntry> list) {
        for (TimeEntry timeEntry : list) {
            System.out.println(timeEntryToString(timeEntry));
        }
        System.out.println("\n");
    }

    public static void MapPrint(Map<?, ?> map) {
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }
    }


    public static void Start() {
        System.out.println("Redmine-standalone");
    }

    public static Integer OptionsList() {
        System.out.println("Параметры выбора задач: 1 - По проектам | 2 - По конкретным пользователям | 0 - Выход");
        return Integer.parseInt(in.nextLine());
    }

    public static void PrintError() {
        System.out.println("Некорректный ввод\n");
    }

    public static Project ProjectChoose() {
        System.out.println("Список проектов");
        List<Project> projects = redmineFiler.getProjectsList();
        for (Project project : projects) {
            System.out.print(project.getName() + "(" + project.getId() + ")" + "\t");
        }
        System.out.println("\n");

        System.out.println("Выберите проект");
        String currentProjectName = in.nextLine();
        for (Project project : projects) {
            if (project.getName().equals(currentProjectName))
                return project;
        }
        System.out.println("Неверно выбранный проект");
        return null;
    }

    public static void ProjectName(Project project) {
        System.out.println("Проект: " + project.getName());
    }

    public static Integer ProjectSearchOptionsList() {
        System.out.println("Опции: 1 - Все задачи | 2 - Степень выполения более 50% | " +
                "3 - Степень выполнения менее 50% | 4 - По статусу (По умолчанию в работе) | " +
                "5 - По приоритету (По умолчанию нормальный) | 0 - Выход");
        return Integer.parseInt(in.nextLine());
    }

    public static String StatusChoose() {
        for (String status : statusList)
            System.out.println(status + "\t");
        System.out.println("Выберите статус задачи");

        String selectedStatus = in.nextLine();
        if (statusList.contains(selectedStatus)) {
            return selectedStatus;
        }
        System.out.println("Неверно выбранный статус, выставлено значение по умолчанию");
        return "В работе";
    }

    public static String PriorityChoose() {
        for (String priority : priorityList)
            System.out.println(priority + "\t");
        System.out.println("Выберите приоритет задачи");

        String selectedPriority = in.nextLine();
        if (priorityList.contains(selectedPriority)) {
            return selectedPriority;
        }
        System.out.println("Неверно выбранный приоритет задача, выставлено значение по умолчанию");
        return "Нормальный";
    }

    public static Integer UserSearchOptionsList() {
        System.out.println("Опции: 1 - Все задачи | 2 - По временному промежутку | " +
                "3 - Дни с трудозатратами меньше 8 часов (по ВП) | 4 - Недели с трудозатратами меньше 40 часов | " +
                "0 - Выход");
        return Integer.parseInt(in.nextLine());
    }

    public static User UserChoose() {
        List<User> users = redmineFiler.getUsersList();
        System.out.println("Введите логин пользователя");
        String userLogin = in.nextLine();
        for (User user : users) {
            if (user.getLogin().equals(userLogin)) {
                return user;
            }
        }
        System.out.println("Неверно выбранный пользователь");
        return null;
    }

    public static String DateChoose() {
        System.out.println("Введите дату");
        String dateString = in.nextLine();
        SimpleDateFormat inputDateFormat = new SimpleDateFormat("dd.MM.yyyy");
        SimpleDateFormat outputDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = inputDateFormat.parse(dateString);
            return outputDateFormat.format(date);
        } catch (Exception e) {
            System.out.println("Неверно введенная дата");
            return null;
        }
    }

}
