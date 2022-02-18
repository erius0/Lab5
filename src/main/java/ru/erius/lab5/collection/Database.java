package ru.erius.lab5.collection;

public interface Database {

    void load() throws DatabaseLoadFailedException;

    void save() throws DatabaseSaveFailedException;

    class DatabaseLoadFailedException extends Exception {

        public DatabaseLoadFailedException(String message) {
            super(message);
        }

        public DatabaseLoadFailedException(String message, String path) {
            super(String.format(message, path));
        }

        public DatabaseLoadFailedException(String message, Throwable cause) {
            super(message, cause);
        }

        public DatabaseLoadFailedException(String message, String path, Throwable cause) {
            super(String.format(message, path), cause);
        }

        public DatabaseLoadFailedException(Throwable cause) {
            super(cause);
        }
    }

    class DatabaseSaveFailedException extends Exception {

        public DatabaseSaveFailedException(String message) {
            super(message);
        }

        public DatabaseSaveFailedException(String message, String path) {
            super(String.format(message, path));
        }

        public DatabaseSaveFailedException(String message, Throwable cause) {
            super(message, cause);
        }

        public DatabaseSaveFailedException(String message, String path, Throwable cause) {
            super(String.format(message, path), cause);
        }

        public DatabaseSaveFailedException(Throwable cause) {
            super(cause);
        }
    }
}
