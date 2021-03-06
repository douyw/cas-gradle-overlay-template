package org.apereo.cas.infusionsoft.dao;

import org.apereo.cas.infusionsoft.domain.LoginAttempt;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Date;
import java.util.List;

public interface LoginAttemptDAO extends PagingAndSortingRepository<LoginAttempt, Long> {
    List<LoginAttempt> findByUsernameAndDateAttemptedGreaterThanOrderByDateAttemptedDesc(String username, Date dateAttempted);

    List<LoginAttempt> findByDateAttemptedLessThan(Date dateAttempted);
}
