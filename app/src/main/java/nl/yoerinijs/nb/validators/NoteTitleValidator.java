package nl.yoerinijs.nb.validators;

/**
 * A simple note title validator
 */
public class NoteTitleValidator {
    public static boolean isNoteTitleValid(String noteTitle) {
        return noteTitle.length() >= 4;
    }
}
