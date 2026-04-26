package Service.TransactionsLogic;

@FunctionalInterface
public interface UnitOfWork {
    void execute() throws Exception;
}