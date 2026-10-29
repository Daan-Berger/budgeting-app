package com.daanberger.budgeting.parsing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.daanberger.budgeting.controllers.dto.RawTransactionDto;

@ExtendWith(MockitoExtension.class)
public class BelfiusCsvParserTests {

    private BelfiusCsvParser parser;

    @BeforeEach
    void setup() {
        parser = new BelfiusCsvParser();
    }

    @Test
    void parseDto_shouldParseMultipleTransactionTypesCorrectly() {
        String csvContent = """
                Boekingsdatum vanaf;
                Boekingsdatum tot en met;
                Bedrag vanaf;
                Bedrag tot en met;
                Rekeninguittrekselnummer vanaf;
                Rekeninguittrekselnummer tot en met;
                Mededeling;
                Naam tegenpartij bevat;
                Rekening tegenpartij;
                Laatste saldo;1.528,23 EUR
                Datum/uur van het laatste saldo;26/10/2026 17:24:13
                ;
                Rekening;Boekingsdatum;Rekeninguittrekselnummer;Transactienummer;Rekening tegenpartij;Naam tegenpartij bevat;Straat en nummer;Postcode en gemeente;Transactie;Valutadatum;Bedrag;Devies;BIC;Landcode;Mededelingen
                BE12 0000 0000 0000;26/10/2026;;;;SAMPLE STORE 999999;;1000  BRUSSEL;BANCONTACT-AANKOOP - SAMPLE STORE 999999 - 1000        BRUSSEL BE - 26/10/26 12:30 - CONTACTLOOS VIA WALLET   APPLE PAY - KAART 5169 20XX XXXX 0000 - Janssens Jan    REF. : 0012345678901 VAL. 26-10;26/10/2026;-15,50;EUR;;BE;BANCONTACT-AANKOOP - SAMPLE STORE 999999 - 1000        BRUSSEL BE - 26/10/26 12:30 - CONTACTLOOS VIA WALLET   APPLE PAY - KAART 5169 20XX XXXX 0000 - Janssens Jan    REF. : 0012345678901 VAL. 26-10
                BE30 0635 3870 6511;25/10/2026;0012;20261025001;BE68 5390 0754 7034;Werkgever BV;Kerkstraat 1;1000 BRUSSEL;EUROPESE OVERSCHRIJVING;25/10/2026;2 450,75;EUR;GKCCBEBB;BE;Salaris oktober 2026
                BE30 0635 3870 6511;24/10/2026;0011;20261024005;BE12 3456 7890 1234;Vastgoedbeheer NV;;;EUROPESE DOMICILIERING;24/10/2026;-850,00;EUR;GEBABEB1;BE;Huur appartement november
                """;

        InputStream stream = new ByteArrayInputStream(csvContent.getBytes(StandardCharsets.UTF_8));

        List<RawTransactionDto> transactions = parser.parseDto(stream);

        RawTransactionDto firstTransaction = transactions.get(0);
        assertEquals(firstTransaction.date(), LocalDate.of(2026, 10, 26));
        assertEquals(firstTransaction.amount(), new BigDecimal("-15.50"));
        assertEquals(firstTransaction.counterparty(), "SAMPLE STORE 999999");
        assertNull(firstTransaction.counterpartyIban());
        assertTrue(firstTransaction.description().contains("SAMPLE STORE 999999"));

        RawTransactionDto secondTransaction = transactions.get(1);
        assertEquals(secondTransaction.date(), LocalDate.of(2026, 10, 25));
        assertEquals(secondTransaction.amount(), new BigDecimal("2450.75"));
        assertEquals(secondTransaction.counterparty(), "Werkgever BV");
        assertEquals(secondTransaction.counterpartyIban(), "BE68 5390 0754 7034");
        assertEquals(secondTransaction.description(), "Salaris oktober 2026");

        RawTransactionDto thirdTransaction = transactions.get(2);
        assertEquals(thirdTransaction.date(), LocalDate.of(2026, 10, 24));
        assertEquals(thirdTransaction.amount(), new BigDecimal("-850.00"));
        assertEquals(thirdTransaction.counterparty(), "Vastgoedbeheer NV");
        assertEquals(thirdTransaction.counterpartyIban(), "BE12 3456 7890 1234");
        assertEquals(thirdTransaction.description(), "Huur appartement november");
    }

    @Test
    void parseDto_shouldThrowRuntimeExceptionForInvalidTransactionData() {
        String csvContent = """
                Rekening;Boekingsdatum;Rekeninguittrekselnummer;Transactienummer;Rekening tegenpartij;Naam tegenpartij bevat;Straat en nummer;Postcode en gemeente;Transactie;Valutadatum;Bedrag;Devies;BIC;Landcode;Mededelingen
                BE30 0635 3870 6511;invalid-date;;;;SAMPLE STORE;;1000 BRUSSEL;BANCONTACT-AANKOOP;26/10/2026;-10,00;EUR;;BE;Invalid transaction
                """;

        InputStream stream = new ByteArrayInputStream(csvContent.getBytes(StandardCharsets.UTF_8));

        assertThrows(RuntimeException.class, () -> parser.parseDto(stream));
    }
}
