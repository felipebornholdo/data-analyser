package com.br.dataanalyser.process;

import com.br.dataanalyser.infrastructure.Messages;
import com.br.dataanalyser.service.AnalyserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class SalesReportProcess {

    private final AnalyserService analyserService;
    private final Messages messages;

    private static final Logger LOGGER = LoggerFactory.getLogger(SalesReportProcess.class);

    @Autowired
    public SalesReportProcess(AnalyserService analyserService,
                              Messages messages) {
        this.analyserService = analyserService;
        this.messages = messages;
    }

    @Scheduled(cron = "* * * * * ?")
    public void processFile() {
        try {
            LOGGER.info("Sales report process started");
            this.analyserService.analyzeFile();
            LOGGER.info("Sales report process finished");
        } catch (Exception e) {
            LOGGER.error(messages.get("file.process.error", e.getLocalizedMessage()));
        }
    }
}
