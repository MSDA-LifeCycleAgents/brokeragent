package com.mlaf.hu.decisionagent;

import com.mlaf.hu.models.InstructionSet;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.Date;

public class InstructionSetTest {
    private InstructionSet is;

    @Before
    public void setUp() throws Exception {
        this.is = new InstructionSet();
        this.is.setHeartbeatTimePattern("0 45 23 * * *");
    }

    @Test
    public void nextDate() {
        assert this.is != null;
        Date nextDate = this.is.nextDate();
        assert nextDate != null;
        assert nextDate.toString().equals(String.format("Tue Dec %s 23:45:00 CET 2017", LocalDateTime.now().getDayOfMonth()));
    }
}