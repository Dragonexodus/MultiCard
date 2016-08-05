package presentation.controls;

import javafx.scene.control.TextField;

import java.util.Optional;

/**
 * Created by Patrick on 07.07.2015.
 */
public class NumericTextField extends TextField {
    private int maxlength;

    private Optional<Integer> defaultValue;

    public NumericTextField() {
        this.maxlength = 10;
        this.defaultValue = Optional.empty();

        this.focusedProperty().addListener(x -> {
            if (getText().length() == 0 && this.defaultValue.isPresent()) {
                replaceText(0, 0, this.defaultValue.get().toString());
            }
        });
    }

    public void setMaxlength(int maxlength) {
        this.maxlength = maxlength;
    }

    public void setDefaultValue(int value) {
        this.defaultValue = Optional.of(value);
    }

    @Override
    public void replaceText(int start, int end, String text) {
        if (this.validate(start, end, text)) {
            super.replaceText(start, end, text);
        }
    }

    @Override
    public void replaceSelection(String text) {
        if ("".equals(text)) {
            super.replaceSelection(text);
        }

        if (text.matches("[0-9]") && this.getText().length() < maxlength) {
            super.replaceSelection(text);
        }
    }

    private boolean validate(int start, int end, String text) {
        if ("".equals(text)) {
            return true;
        }

        if (!text.matches("[0-9]")) {
            return false;
        }

        if (this.getText().length() < maxlength) {
            return true;
        }

        if (getText().length() - (end - start) + text.length() <= this.maxlength) {
            return true;
        }
        return false;
    }
}