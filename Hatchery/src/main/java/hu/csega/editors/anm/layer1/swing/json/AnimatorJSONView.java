package hu.csega.editors.anm.layer1.swing.json;

import hu.csega.editors.anm.layer4.data.model.AnimatorModel;
import hu.csega.games.units.UnitStore;

import java.awt.*;

import javax.swing.*;

import org.json.JSONException;
import org.json.JSONObject;

public class AnimatorJSONView extends JPanel {

    private final AnimatorModel model;

    private final JScrollPane scrollPane;
    private final JTextArea jsonText;
    private final JButton saveButton;

    public AnimatorJSONView() {
        this.model = UnitStore.instance(AnimatorModel.class);

        this.jsonText = new JTextArea();
        this.scrollPane = new JScrollPane(this.jsonText);
        this.saveButton = new JButton("Save this stuff!");

        this.setLayout(new BorderLayout());
        this.add(this.scrollPane, BorderLayout.CENTER);
        this.add(this.saveButton, BorderLayout.SOUTH);

        this.saveButton.addActionListener(event -> { this.model.changeJSON(this.jsonText.getText()); });
    }

    public void setJSON(String json) {
        jsonText.setText(json);
    }

    private static final long serialVersionUID = 1L;
}
