package store.bookscamp.front.common.exception;

// RuntimeException을 상속받아 Unchecked Exception으로 만듭니다.
public class FileStorageException extends RuntimeException {
    public FileStorageException(String message) {
        super(message);
    }

    public FileStorageException(String message, Throwable cause) {
        super(message, cause); // 원인 에러(cause)를 함께 넘겨야 디버깅이 가능합니다.
    }
}