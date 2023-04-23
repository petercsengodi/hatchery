package hu.csega.editors.anm.layer1.swing.json;

import java.awt.*;

import javax.swing.*;

import org.json.JSONException;
import org.json.JSONObject;

public class AnimatorJSONView extends JPanel {

    private final JScrollPane scrollPane;
    private final JTextArea jsonText;
    private final JButton saveButton;

    public AnimatorJSONView() {
        this.jsonText = new JTextArea();
        this.scrollPane = new JScrollPane(this.jsonText);
        this.saveButton = new JButton("Save this stuff!");

        this.setLayout(new BorderLayout());
        this.add(this.scrollPane, BorderLayout.CENTER);
        this.add(this.saveButton, BorderLayout.SOUTH);
    }

    public void setJSON(Object object) {
        try {
            JSONObject json = new JSONObject(object);
            String text = json.toString(2);
            jsonText.setText(text);
        } catch(JSONException ex) {
            jsonText.setText("Exception should not have occurred at this point, but it had.");
        }
    }

    private static final long serialVersionUID = 1L;
}
