import com.taskadapter.redmineapi.bean.Project;
import com.taskadapter.redmineapi.bean.User;

public class RedmineExample {
    public static void main(String[] args) {

        RedmineFiler redmineFiler = new RedmineFiler();
        boolean isExit = false;

        while ((!isExit)) {
            int option = UI.OptionsList();

            switch (option) {
                case 1 -> {
                    Project currentProject = UI.ProjectChoose();
                    int ProjectSearchOption = UI.ProjectSearchOptionsList();

                    if (currentProject != null) {
                        switch (ProjectSearchOption) {
                            case 1 -> UI.IssuesPrint(redmineFiler.SearchAllIssues(currentProject.getId()));
                            case 2 -> UI.IssuesPrint(redmineFiler.SearchByMoreHalfDoneRatioIssues(currentProject.getId()));
                            case 3 -> UI.IssuesPrint(redmineFiler.SearchByLessHalfDoneRatioIssues(currentProject.getId()));
                            case 4 -> {
                                String selectedStatus = UI.StatusChoose();
                                UI.IssuesPrint(redmineFiler.SearchByStatusIssues(currentProject.getId(),
                                        redmineFiler.getStatusId(selectedStatus)));
                            }
                            case 5 -> {
                                String selectedPriority = UI.PriorityChoose();
                                UI.IssuesPrint(redmineFiler.SearchByPriorityIssues(currentProject.getId(),
                                        redmineFiler.getPriorityId(selectedPriority)));
                            }
                            case 0 -> isExit = true;
                            default -> UI.PrintError();
                        }
                    }
                }
                case 2 -> {
                    User currentUser = UI.UserChoose();
                    int UserSearchOption = UI.UserSearchOptionsList();

                    if (currentUser != null) {
                        switch (UserSearchOption) {
                            case 1 -> UI.IssuesPrint(redmineFiler.SearchAllAssignedIssues(currentUser.getId()));
                            case 2 -> {
                                String fromDate = UI.DateChoose();
                                String toDate = UI.DateChoose();
                                UI.TimeEntriesPrint(redmineFiler.SearchByAssignedAndDateIssues(currentUser.getId(),
                                        fromDate, toDate));
                            }
                            case 3 -> {
                                String fromDate = UI.DateChoose();
                                String toDate = UI.DateChoose();
                                UI.MapPrint(redmineFiler.SearchLess8HourDays(currentUser.getId(),
                                        fromDate, toDate));
                            }
                            case 4 -> {
                                Integer year;
                                Integer month;
                                UI.MapPrint(redmineFiler.SearchLess40HourDWeeks(currentUser.getId(),
                                        2023, 3));
                            }
                            case 0 -> isExit = true;
                            default -> UI.PrintError();
                        }
                    }
                }
                case 0 -> isExit = true;
                default -> UI.PrintError();
            }
        }
    }
}
