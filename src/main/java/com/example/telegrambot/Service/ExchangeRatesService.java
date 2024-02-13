package com.example.telegrambot.Service;

import com.example.telegrambot.exception.ServiceException;

public interface ExchangeRatesService {
    String getUSDExchangeRate () throws ServiceException;
    String getEURExchangeRate () throws ServiceException;
}
