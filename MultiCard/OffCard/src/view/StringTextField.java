package view;

/**
 * Created by Patrick on 21.08.2015.
 */
public class StringTextField extends javafx.scene.control.TextField {
    private int maxlength;

    public StringTextField() {
        this.maxlength = Integer.MAX_VALUE;
    }

    public void setMaxlength(int maxlength) {
        this.maxlength = maxlength;
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

        if (this.getText().length() < maxlength) {
            return true;
        }

        if (getText().length() - (end - start) + text.length() <= this.maxlength) {
            return true;
        }

        return false;
    }
}
