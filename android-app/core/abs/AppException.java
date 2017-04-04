package dubsapp.core.abs;

abstract class AppException extends Throwable {

    @Override
    public String toString() {
        //add log!s
        return super.toString();
    }

//    public static final class Exception extends AppException {
//        public static void raise(String detailMessage) {
//            raise(new java.lang.Exception(detailMessage));
//        }
//        public static void raise(java.lang.Exception ex) {
//            raise(new RuntimeException(ex));
//        }
//
//        public static void raise(RuntimeException exception) {
//            throw exception;
//        }
//    }

}

//
//    public static Throwable getRootCause(Throwable throwable) {
//        if (throwable.getCause() != null)
//            return getRootCause(throwable.getCause());
//
//        return throwable;
//    }