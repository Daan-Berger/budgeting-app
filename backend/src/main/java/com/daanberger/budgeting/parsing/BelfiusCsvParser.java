package com.daanberger.budgeting.parsing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;

import com.daanberger.budgeting.controllers.dto.RawTransactionDto;
import com.daanberger.budgeting.exceptions.BelfiusCsvParseException;

@Component
public class BelfiusCsvParser implements BankCsvParser {
    private static final DateTimeFormatter BELFIUS_DATE = DateTimeFormatter.ofPattern("dd/MM/uuuu")
            .withResolverStyle(ResolverStyle.STRICT);

    @Override
    public List<RawTransactionDto> parseDto(InputStream stream) {
        List<RawTransactionDto> transactions = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(stream, StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Rekening;Boekingsdatum")) {
                    break;
                }
            }

            if (line == null) {
                throw new BelfiusCsvParseException("Invalid Belfius CSV: Table header row not found.");
            }

            CSVFormat format = CSVFormat.Builder.create(CSVFormat.DEFAULT)
                    .setDelimiter(";")
                    .setHeader(line.split(";"))
                    .setSkipHeaderRecord(false)
                    .setIgnoreHeaderCase(true)
                    .setTrim(true)
                    .get();

            try (CSVParser parser = CSVParser.parse(reader, format)) {
                for (CSVRecord record : parser) {
                    LocalDate date = LocalDate.parse(
                            record.get("Boekingsdatum").trim(),
                            BELFIUS_DATE);

                    String amount = record.get("Bedrag")
                            .replaceAll("[\\p{Z}]", "")
                            .replace(",", ".");

                    BigDecimal SanitizedAmount = new BigDecimal(amount);

                    String counterParty = record.get("Naam tegenpartij bevat");

                    String counterpartyIban = record.get("Rekening tegenpartij");
                    if (record.get("Rekening tegenpartij").isEmpty()) {
                        counterpartyIban = null;
                    }

                    String description = record.get("Mededelingen");

                    RawTransactionDto dto = new RawTransactionDto(date, SanitizedAmount, counterParty, counterpartyIban,
                            description);

                    transactions.add(dto);
                }
            }

        } catch (DateTimeParseException | IllegalArgumentException | IOException e) {
            throw new BelfiusCsvParseException(
                    "Invalid Belfius CSV transaction data", e);
        }

        return transactions;
    }

}
