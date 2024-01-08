package at.ac.tuwien.sepr.groupphase.backend.scheduler.impl;

import at.ac.tuwien.sepr.groupphase.backend.scheduler.ExpenseScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ExpenseSchedulerImpl implements ExpenseScheduler {

    @Override
    @Scheduled(cron = "0 0 0 * * *")
    public void createRepeatingExpense() {

    }
}
