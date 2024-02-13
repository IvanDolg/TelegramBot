package com.example.telegrambot.Service.impl;

import com.example.telegrambot.Service.ExchangeRatesService;
import com.example.telegrambot.client.CbrClient;
import com.example.telegrambot.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xml.sax.InputSource;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;
import org.w3c.dom.Document;

@Service
public class ExchangeRatesServiceImpl implements ExchangeRatesService {

    private static final String USD_XPATH = "/ValCurs//Valute[@ID='R01235']/Value";
    private static final String EUR_XPATH = "/ValCurs//Valute[@ID='R01239']/Value";

    @Autowired
    private CbrClient cbrClient;
    @Override
    public String getUSDExchangeRate() throws ServiceException {
        var xml = cbrClient.getCurrencyRatesXML();
        return extractCurrencyValueFromXML(xml, USD_XPATH);
    }

    @Override
    public String getEURExchangeRate() throws ServiceException {
        var xml = cbrClient.getCurrencyRatesXML();
        return extractCurrencyValueFromXML(xml, EUR_XPATH);
    }

    private static String extractCurrencyValueFromXML(String xml, String xpathExpression) throws ServiceException {
        var source = new InputSource(new StringReader(xml));
        try {
            var xpath = XPathFactory.newInstance().newXPath();
            var document = (Document) xpath.evaluate("/", source, XPathConstants.NODE);
            return xpath.evaluate(xpathExpression, document);

        } catch (XPathExpressionException e) {
            throw new ServiceException("Не удалось распарсить XML", e);
        }
    }
}
