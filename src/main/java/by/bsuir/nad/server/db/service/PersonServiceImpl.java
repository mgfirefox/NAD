package by.bsuir.nad.server.db.service;

import by.bsuir.nad.server.db.dao.PersonDao;
import by.bsuir.nad.server.db.dao.PersonDaoImpl;
import by.bsuir.nad.server.db.entity.Person;
import lombok.NonNull;

public class PersonServiceImpl extends ServiceImpl<Person, Long> implements PersonService {
    @NonNull
    private final PersonDao dao;

    public PersonServiceImpl() {
        this(new PersonDaoImpl());
    }
    
    public PersonServiceImpl(@NonNull PersonDao dao) {
        super(dao);
        this.dao = dao;
    }
}
