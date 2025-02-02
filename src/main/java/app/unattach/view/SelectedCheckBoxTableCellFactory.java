package app.unattach.view;

import app.unattach.model.EmailStatus;
import app.unattach.model.Email;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;

public class SelectedCheckBoxTableCellFactory
    implements Callback<TableColumn.CellDataFeatures<Email, CheckBox>, ObservableValue<CheckBox>> {
  @Override
  public ObservableValue<CheckBox> call(TableColumn.CellDataFeatures<Email, CheckBox> cellDataFeatures) {
    Email email = cellDataFeatures.getValue();
    CheckBox checkBox = new CheckBox();
    TableView<Email> tableView = cellDataFeatures.getTableView();
    boolean enabled = tableView.isEditable() && email.getStatus() != EmailStatus.FAILED &&
        email.getStatus() != EmailStatus.PROCESSED;
    checkBox.setDisable(!enabled);
    checkBox.selectedProperty().setValue(email.isSelected());
    checkBox.selectedProperty().addListener((observableValue, oldValue, newValue) -> {
      EmailStatus targetStatus = newValue ? EmailStatus.TO_PROCESS : EmailStatus.NOT_SELECTED;
      ObservableList<Email> selectedEmails = tableView.getSelectionModel().getSelectedItems();
      if (selectedEmails.contains(email)) {
        for (Email selectedEmail : selectedEmails) {
          selectedEmail.setStatus(targetStatus);
        }
      } else {
        email.setStatus(targetStatus);
      }
      tableView.refresh();
    });
    return new SimpleObjectProperty<>(checkBox);
  }
}