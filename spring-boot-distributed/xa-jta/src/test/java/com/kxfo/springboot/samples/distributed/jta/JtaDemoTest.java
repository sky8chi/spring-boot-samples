package com.kxfo.springboot.samples.distributed.jta;

import com.kxfo.springboot.samples.distributed.jta.dto.TransferLog;
import com.kxfo.springboot.samples.distributed.jta.services.AuditService;
import com.kxfo.springboot.samples.distributed.jta.services.BankAccountService;
import com.kxfo.springboot.samples.distributed.jta.services.TellerService;
import com.kxfo.springboot.samples.distributed.jta.services.TestHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * TODO
 *
 * @author by tianxiang.chi
 * @date 2020-02-10 13:02
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = JtaDemoApplication.class)
public class JtaDemoTest {
    @Autowired
    TestHelper testHelper;

    @Autowired
    TellerService tellerService;

    @Autowired
    BankAccountService accountService;

    @Autowired
    AuditService auditService;

    @Before
    public void beforeTest() throws Exception {
        testHelper.runAuditDbInit();
        testHelper.runAccountDbInit();
    }

    /**
     * a0000001 1000 往 a0000002账号 2000 打 500
     * @throws Exception
     */
    @Test
    public void givenAnnotationTx_whenNoException_thenAllCommitted()
            throws Exception {
        tellerService.executeTransfer("a0000001", "a0000002",
                BigDecimal.valueOf(500));

        assertThat(accountService.balanceOf("a0000001"))
                .isEqualByComparingTo(BigDecimal.valueOf(500));
        assertThat(accountService.balanceOf("a0000002"))
                .isEqualByComparingTo(BigDecimal.valueOf(2500));

        TransferLog lastTransferLog = auditService.lastTransferLog();
        assertThat(lastTransferLog).isNotNull();
        assertThat(lastTransferLog.getFromAccountId()).isEqualTo("a0000001");
        assertThat(lastTransferLog.getToAccountId()).isEqualTo("a0000002");
        assertThat(lastTransferLog.getAmount())
                .isEqualByComparingTo(BigDecimal.valueOf(500));
    }

    @Test
    public void givenAnnotationTx_whenException_thenAllRolledBack()
            throws Exception {
        assertThatThrownBy(() -> {
            tellerService.executeTransfer("a0000002", "a0000001",
                    BigDecimal.valueOf(100000));
        }).hasMessage("余额不足!");

        assertThat(accountService.balanceOf("a0000001"))
                .isEqualByComparingTo(BigDecimal.valueOf(1000));
        assertThat(accountService.balanceOf("a0000002"))
                .isEqualByComparingTo(BigDecimal.valueOf(2000));
        assertThat(auditService.lastTransferLog()).isNull();
    }

    @Test
    public void givenProgrammaticTx_whenCommit_thenAllCommitted()
            throws Exception {
        tellerService.executeTransferProgrammaticTx("a0000001", "a0000002",
                BigDecimal.valueOf(500));

        assertThat(accountService.balanceOf("a0000001"))
                .isEqualByComparingTo(BigDecimal.valueOf(500));
        assertThat(accountService.balanceOf("a0000002"))
                .isEqualByComparingTo(BigDecimal.valueOf(2500));

        TransferLog lastTransferLog = auditService.lastTransferLog();
        assertThat(lastTransferLog).isNotNull();
        assertThat(lastTransferLog.getFromAccountId()).isEqualTo("a0000001");
        assertThat(lastTransferLog.getToAccountId()).isEqualTo("a0000002");
        assertThat(lastTransferLog.getAmount())
                .isEqualByComparingTo(BigDecimal.valueOf(500));
    }

    @Test
    public void givenProgrammaticTx_whenRollback_thenAllRolledBack()
            throws Exception {
        assertThatThrownBy(() -> {
            tellerService.executeTransferProgrammaticTx("a0000002", "a0000001",
                    BigDecimal.valueOf(100000));
        }).hasMessage("余额不足!");

        assertThat(accountService.balanceOf("a0000001"))
                .isEqualByComparingTo(BigDecimal.valueOf(1000));
        assertThat(accountService.balanceOf("a0000002"))
                .isEqualByComparingTo(BigDecimal.valueOf(2000));
        assertThat(auditService.lastTransferLog()).isNull();
    }
}
