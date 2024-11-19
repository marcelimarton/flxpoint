package com.example.flxpoint.assessment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.example.flxpoint.assessment.entities.CustomerCrm;
import com.example.flxpoint.assessment.services.CrmProcessor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@EnableScheduling
@SpringBootApplication
public class QueueProcessorApplication implements CommandLineRunner {

	private static final Logger logger = LoggerFactory.getLogger(QueueProcessorApplication.class);

	private static final String COMMAND_UPDATE = "UPDATE";
	private static final String COMMAND_DELETE = "DELETE";
	private static final String COMMAND_CREATE = "CREATE";

	private String baseDir = new File("").getAbsolutePath();
	private String queueDir = baseDir + "\\queue\\";

	public static void main(String[] args) {
		SpringApplication.run(QueueProcessorApplication.class, args);
	}

	@Override
	public void run(String... args) {
		processQueue();
	}

	private static final DateTimeFormatter FILENAME_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH_mm_ss");
	private static final Pattern FILENAME_PATTERN = Pattern.compile("(\\d{4}-\\d{2}-\\d{2}-\\d{2}_\\d{2}_\\d{2})");

	public void processQueue() {
		new QueueProcessor(queueDir).run();

	}

	private void processOldestFile(File[] jsonFiles) {
		// Initialize the variable to hold the oldest file
		File oldestFile = jsonFiles[0];

		// Loop through the files to find the oldest one
		for (File file : jsonFiles) {
			// Check if the current file is older than the oldestFile found
			LocalDateTime currFileDateTime = extractDateTimeFromFileName(file.getName().substring(0, 19));
			LocalDateTime oldestFileDateTime = extractDateTimeFromFileName(oldestFile.getName().substring(0, 19));
			if (currFileDateTime.isBefore(oldestFileDateTime)) {
				oldestFile = file;
			}
		}

		// Process the oldest file
		processFile(oldestFile);
	}

	private LocalDateTime extractDateTimeFromFileName(String fileName) {
		Matcher matcher = FILENAME_PATTERN.matcher(fileName);
		if (matcher.find()) {
			String dateTimeStr = matcher.group(1); // No need to replace underscores with spaces now
			return LocalDateTime.parse(dateTimeStr, FILENAME_DATE_FORMAT);
		}
		throw new IllegalArgumentException("Filename does not match expected format: " + fileName);
	}

	private void processFile(File file) {
		try {
			// Read file content
			String content = new String(Files.readAllBytes(file.toPath()));
			logger.info("Processing file: " + file.getName());
			logger.info("Content: " + content);

			Gson gson = new GsonBuilder().create();

			JsonObject jsonObject = JsonParser.parseString(content).getAsJsonObject();

			String command = jsonObject.get("command").toString().replace("\"", "");
			CrmProcessor processor = new CrmProcessor();

			try {
				if (command.equals(COMMAND_CREATE)) {
					CustomerCrm data = gson.fromJson(jsonObject.getAsJsonObject("data").toString(), CustomerCrm.class);
					processor.createCRMRecord(data);
				} else if (command.equals(COMMAND_UPDATE)) {
					CustomerCrm data = gson.fromJson(jsonObject.getAsJsonObject("data").toString(), CustomerCrm.class);
					processor.updateCRMRecord(data);
				}
				if (command.equals(COMMAND_DELETE)) {
					String customerId = gson.fromJson(jsonObject.getAsJsonObject("customerId").toString(),
							String.class);
					processor.deleteCRMRecord(customerId);
				}
			} catch (Exception ex) {
				logger.error("CRM Server not responding " + ex.getMessage());
				return;
			}

			Files.move(file.toPath(), Paths.get(baseDir + "\\processed\\" + file.getName()));

		} catch (IOException e) {
			logger.error("Error processing file: " + file.getName() + " - " + e.getMessage());
		}
	}

	class QueueProcessor implements Runnable {
		private final String queueDir;

		public QueueProcessor(String queueDir) {
			this.queueDir = queueDir;
		}

		@Override
		public void run() {
			File dir = new File(queueDir);
			if (!dir.exists() || !dir.isDirectory()) {
				logger.error("Directory not found: " + queueDir);
				return;
			}

			while (true) {
				File[] jsonFiles = dir.listFiles((d, name) -> name.endsWith(".json"));
				if (jsonFiles != null && jsonFiles.length > 0) {
					processOldestFile(jsonFiles);
				}

				try {
					Thread.sleep(1000); // Adjust sleep time as needed
				} catch (InterruptedException e) {
					logger.error(e.getMessage());
					Thread.currentThread().interrupt(); // Restore interrupted status
					return;
				}
			}
		}
	}
}
