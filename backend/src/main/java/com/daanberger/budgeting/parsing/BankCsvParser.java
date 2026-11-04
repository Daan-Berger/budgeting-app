package com.daanberger.budgeting.parsing;

import java.io.InputStream;
import java.util.List;

import com.daanberger.budgeting.controllers.dto.RawTransactionDto;

public interface BankCsvParser {
    List<RawTransactionDto> parseDto(InputStream stream);
}
