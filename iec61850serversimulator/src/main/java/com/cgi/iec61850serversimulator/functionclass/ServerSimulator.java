package com.cgi.iec61850serversimulator.functionclass;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.beanit.openiec61850.SclParseException;
import com.beanit.openiec61850.SclParser;
import com.beanit.openiec61850.ServerModel;
import com.beanit.openiec61850.ServerSap;
import com.beanit.openiec61850.internal.cli.Action;
import com.beanit.openiec61850.internal.cli.ActionProcessor;
import com.beanit.openiec61850.internal.cli.CliParameter;
import com.beanit.openiec61850.internal.cli.CliParameterBuilder;
import com.beanit.openiec61850.internal.cli.CliParseException;
import com.beanit.openiec61850.internal.cli.CliParser;
import com.beanit.openiec61850.internal.cli.IntCliParameter;
import com.beanit.openiec61850.internal.cli.StringCliParameter;
import com.cgi.iec61850serversimulator.dataclass.Device;
import com.cgi.iec61850serversimulator.dataclass.Relay;
import com.cgi.iec61850serversimulator.dataclass.Schedule;
import com.cgi.iec61850serversimulator.datarepository.RelayRepository;
import com.cgi.iec61850serversimulator.datarepository.ScheduleRepository;

@EntityScan("com.cgi.iec61850serversimulator.datamodel")
@EnableJpaRepositories(basePackages = "com.cgi.iec61850serversimulator.datarepository")
@SpringBootApplication
@EnableAutoConfiguration
public class ServerSimulator implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(ServerSimulator.class);

    private static final String PRINT_SERVER_MODEL_KEY = "p";
    private static final String PRINT_SERVER_MODEL_KEY_DESCRIPTION = "print server's model";
    private static final String DEVICE_SHOW_MODEL = "d";
    private static final String DEVICE_SHOW_MODEL_DESCRIPTION = "print device object";

    private static final IntCliParameter portParam = new CliParameterBuilder("-p").setDescription(
            "The port to listen on. On unix based systems you need root privilages for ports < 1000. Default: 102")
            .buildIntParameter("port", 10102);

    private static final StringCliParameter modelFileParam = new CliParameterBuilder("-m")
            .setDescription("The SCL file that contains the server's information model.")
            .setMandatory()
            .buildStringParameter("model-file");

    private ServerSap serverSap = null;

    @Autowired
    private RelayRepository relayRepository;
    @Autowired
    private ScheduleRepository scheduleRepository;

    public static void main(final String[] args) {
        SpringApplication.run(ServerSimulator.class, args);
    }

    @Override
    public void run(final String... args) throws Exception {
        logger.info("Applicatie starten...");

        final List<CliParameter> cliParameters = new ArrayList<>();
        cliParameters.add(modelFileParam);
        cliParameters.add(portParam);

        final CliParser cliParser = new CliParser("iec61850bean-console-server", "An IEC 61850 MMS console server.");
        cliParser.addParameters(cliParameters);

        try {
            cliParser.parseArguments(args);
        } catch (final CliParseException e1) {
            logger.error("Error parsing command line parameters {}", e1.getMessage());
            logger.info(cliParser.getUsageString());
            System.exit(1);
        }

        List<ServerModel> serverModels = null;
        try {
            serverModels = SclParser.parse(modelFileParam.getValue());
        } catch (final SclParseException e) {
            logger.error("Error parsing SCL/ICD file", e);
            return;
        }

        logger.info("ServerSap aanmaken...");
        this.serverSap = new ServerSap(portParam.getValue(), 0, null, serverModels.get(0), null);
        logger.info("ServerSap aangemaakt met als poort: {}", portParam.getValue());
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                if (ServerSimulator.this.serverSap != null) {
                    ServerSimulator.this.serverSap.stop();
                }
                logger.info("Server was stopped.");
            }
        });

        // Device initialization by copying from serverModel
        final ServerWrapper serverWrapper = new ServerWrapper(this.serverSap);
        final Device device = new Device();

        final Scheduler scheduler = new Scheduler(device, new SwitchingMomentCalculator(),
                (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(1));

        device.initalizeDevice(serverWrapper);

        // Comparing database with model

        final DatabaseUtils databaseUtils = new DatabaseUtils(serverWrapper);
        databaseUtils.setRelayRepository(this.relayRepository);
        databaseUtils.setScheduleRepository(this.scheduleRepository);
        for (int relayNr = 1; relayNr <= device.getRelays().length; relayNr++) {
            final Relay relay = device.getRelay(relayNr);
            databaseUtils.checkRelay(relay);

            for (int scheduleNr = 1; scheduleNr <= 50; scheduleNr++) {
                final Schedule schedule = relay.getSchedule(scheduleNr);
                databaseUtils.checkSchedule(schedule, relay.getIndexNumber());
            }

        }

        logger.info("SERVER START LISTENING");
        final EventDataListener edl = new EventDataListener(device, scheduler, databaseUtils);
        this.serverSap.startListening(edl);

        // Initial schedule
        try {
            scheduler.calculateTasksForDateTime(device, LocalDateTime.now());
        } catch (final Exception e) {
            logger.warn("Initial switching moment calculation failed, try sending another schedule.", e);
        }

        final ActionProcessor actionProcessor = new ActionProcessor(
                new ActionExecutor(this.serverSap, device, scheduler));
        actionProcessor.addAction(new Action(PRINT_SERVER_MODEL_KEY, PRINT_SERVER_MODEL_KEY_DESCRIPTION));
        actionProcessor.addAction(new Action(DEVICE_SHOW_MODEL, DEVICE_SHOW_MODEL_DESCRIPTION));

        actionProcessor.start();
    }

}
