package com.br.dataanalyser.service;

import com.br.dataanalyser.dto.Customer;
import com.br.dataanalyser.dto.Item;
import com.br.dataanalyser.dto.Sale;
import com.br.dataanalyser.dto.Salesman;
import com.br.dataanalyser.enumeration.DataTypeEnum;
import com.br.dataanalyser.infrastructure.Messages;
import com.br.dataanalyser.infrastructure.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnalyserService {
    
    private static final String DOT_DAT = ".dat";
    private static final String DOT_DONE_DOT_DAT = ".done.dat";
    private static final String REGEX_SPLIT = "(?=ç[0-9A-Z\\[])ç";
    private static final String REGEX_REMOVE_ESPECIAL_CHARACTERS = "\\[|]";

    @Value("${readFolder}")
    private static String readFolder;
    @Value("${writeFolder}")
    private static String writeFolder;
    private final Messages messages;

    @Autowired
    public AnalyserService(Messages messages) {
        this.messages = messages;
    }

    public void analyzeFile() throws BusinessException {
        List<Salesman> salesmen = new ArrayList<>();
        List<Customer> customers = new ArrayList<>();
        List<Sale> sales = new ArrayList<>();
        List<File> newFiles = getNewFiles();

        newFiles.forEach(newFile -> {
            try {
                processFile(newFile, salesmen, customers, sales);
            } catch (IOException processException) {
                throw new UncheckedIOException(processException);
            }
        });

        newFiles.forEach(newFile -> {
            try {
                generateReport(newFile, salesmen, customers, sales);
            } catch (IOException reportException) {
                throw new UncheckedIOException(reportException);
            }
        });
    }

    private void generateReport(File newFile, List<Salesman> salesmen, List<Customer> customers, List<Sale> sales) throws IOException {
        Integer totalCustomers = customers.size();
        Integer totalSellers = salesmen.size();
        Salesman worstSalesman = getWorstSalesman(salesmen, sales);
        Sale mostExpansiveSale = getMostExpansiveSale(sales);
        String fileName = newFile.getName().substring(0, newFile.getName().length() - 4).concat(DOT_DONE_DOT_DAT);
        generateReport(totalCustomers, totalSellers, worstSalesman, mostExpansiveSale, fileName);
    }

    private void processFile(File file, List<Salesman> salesmen, List<Customer> customers, List<Sale> sales) throws IOException {
        try (BufferedReader objReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String strCurrentLine;

            while ((strCurrentLine = objReader.readLine()) != null) {
                List<String> row = Arrays.asList(strCurrentLine.split(REGEX_SPLIT));
                Long dataType = Long.parseLong(row.get(0));
                if (dataType.equals(DataTypeEnum.SALESMAN.getTypeId())) {
                    salesmen.add(populateSalesmanData(row));
                }
                if (dataType.equals(DataTypeEnum.CUSTOMER.getTypeId())) {
                    customers.add(populateCustomerData(row));
                }
                if (dataType.equals(DataTypeEnum.SALE.getTypeId())) {
                    sales.add(populateSalesData(row));
                }
            }
        } catch (IOException e) {
            throw new IOException(messages.get("file.process.error", e.getLocalizedMessage()));
        }

    }

    private List<File> getNewFiles() throws BusinessException {
        try {
            List<File> filesIn = new ArrayList<>();
            List<File> filesOut = new ArrayList<>();
            File fileIn = new File(readFolder);
            File fileOut = new File(writeFolder);

            File[] arrayFilesIn = fileIn.listFiles((dir, name) -> name.endsWith(DOT_DAT));
            if (arrayFilesIn != null) {
                filesIn = Arrays.asList(arrayFilesIn);
            }
            File[] arrayFilesOut = fileOut.listFiles((dir, name) -> name.endsWith(DOT_DONE_DOT_DAT));
            if (arrayFilesOut != null) {
                filesOut = Arrays.asList(arrayFilesOut);
            }
            return getDiferenceBetweenFileLists(filesIn, filesOut);
        } catch (Exception e) {
            throw new BusinessException(messages.get("file.unable.fetch.newFiles", e.getLocalizedMessage()));
        }
    }

    private List<File> getDiferenceBetweenFileLists(List<File> filesIn, List<File> filesOut) {
        return filesIn.stream().filter(fileInStream -> filesOut.stream().noneMatch(fileOutStream -> fileOutStream.getName().equals(
                fileInStream.getName().substring(0, fileInStream.getName().length() - 4).concat(DOT_DONE_DOT_DAT))))
                .collect(Collectors.toList());
    }

    private void generateReport(Integer totalCustomers, Integer totalSellers, Salesman worstSalesman, Sale mostExpansiveSale, String fileName) throws IOException {
        File file = new File(writeFolder + fileName);
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("---------------------------//---------------------------// ------------------------");
            writer.write("\nTotal Customers: ".concat(totalCustomers.toString()));
            writer.write("\nTotal Salesman: ".concat(totalSellers.toString()));
            writer.write("\nWorst Salesman: ".concat(worstSalesman.getName()));
            writer.write("\nMost Expansive Sale: ".concat("ID-").concat(mostExpansiveSale.getSaleId().toString()));
            writer.write("\n---------------------------//---------------------------// ------------------------");
            writer.flush();
        } catch (IOException e) {
            throw new IOException(messages.get("file.generateReport.error", e.getLocalizedMessage()));
        }
    }

    private Sale getMostExpansiveSale(List<Sale> sales) {
        return Collections.max(sales, Comparator.comparing(Sale::getTotalValue));
    }

    private Salesman getWorstSalesman(List<Salesman> salesmen, List<Sale> sales) {
        for (Sale sale : sales) {
            Optional<Salesman> salesman = salesmen.stream().filter(seller -> seller.getName().equals(sale.getSalesman())).findFirst();
            salesman.ifPresent(value -> value.getSales().add(sale));
        }
        salesAdd(salesmen);
        return Collections.min(salesmen, Comparator.comparing(Salesman::getTotalSaleValue));
    }


    private void salesAdd(List<Salesman> salesmen) {
        for (Salesman salesman : salesmen) {
            salesman.setTotalSaleValue(getTotalSaleValue(salesman.getSales()));
        }
    }

    private BigDecimal getTotalSaleValue(List<Sale> sales) {
        BigDecimal totalValue = BigDecimal.ZERO;
        for (Sale sale : sales) {
            totalValue = totalValue.add(sale.getTotalValue());
        }
        return totalValue;
    }


    private Sale populateSalesData(List<String> row) {
        Sale sale = new Sale();
        sale.setSaleId(Long.parseLong(row.get(1)));
        sale.setItems(populateItemsData(row.get(2)));
        sale.setSalesman(row.get(3));
        sale.setTotalValue(getTotalItemsValue(sale.getItems()));
        return sale;
    }

    private BigDecimal getTotalItemsValue(List<Item> items) {
        BigDecimal totalValue = BigDecimal.ZERO;
        for (Item item : items) {
            totalValue = totalValue.add(item.getTotalPrice());
        }
        return totalValue;
    }

    private List<Item> populateItemsData(String items) {
        items = items.replaceAll(REGEX_REMOVE_ESPECIAL_CHARACTERS, "");
        List<Item> filledItems = new ArrayList<>();
        String[] split = items.split(",");
        for (String item : split) {
            Item filledItem = new Item();
            List<String> attributes = Arrays.asList(item.split("-"));
            filledItem.setItemId(Long.parseLong(attributes.get(0)));
            filledItem.setQuantity(Integer.parseInt(attributes.get(1)));
            filledItem.setItemPrice(BigDecimal.valueOf(Double.parseDouble(attributes.get(2))));
            filledItem.setTotalPrice(filledItem.getItemPrice().multiply(new BigDecimal(filledItem.getQuantity())));
            filledItems.add(filledItem);
        }
        return filledItems;
    }

    private Customer populateCustomerData(List<String> row) {
        Customer customer = new Customer();
        customer.setCnpj(row.get(1));
        customer.setName(row.get(2));
        customer.setBusinessArea(row.get(3));
        return customer;
    }

    private Salesman populateSalesmanData(List<String> row) {
        Salesman salesman = new Salesman();
        salesman.setCpf(row.get(1));
        salesman.setName(row.get(2));
        salesman.setSalary(BigDecimal.valueOf(Double.parseDouble(row.get(3))));
        salesman.setSales(new ArrayList<>());
        return salesman;
    }
}
