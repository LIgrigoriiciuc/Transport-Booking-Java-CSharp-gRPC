package Service;

import Domain.Office;
import Repository.OfficeRepository;

public class OfficeService extends GenericService<Long, Office>{
    public OfficeService(OfficeRepository repository) {
        super(repository);
    }
}
