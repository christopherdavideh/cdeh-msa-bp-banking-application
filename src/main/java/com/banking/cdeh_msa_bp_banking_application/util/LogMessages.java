package com.banking.cdeh_msa_bp_banking_application.util;

public class LogMessages {

    // Customer Service Messages
    public static final String CUSTOMER_CREATE_START = "Processing create customer request for identification: {}";
    public static final String CUSTOMER_CREATE_SUCCESS = "Customer service successfully created customer with ID: {}";
    public static final String CUSTOMER_CREATE_ERROR = "Error creating customer: {}";

    public static final String CUSTOMER_GET_BY_ID_START = "Processing get customer request for ID: {}";
    public static final String CUSTOMER_GET_BY_ID_SUCCESS = "Customer service successfully retrieved customer: {}";
    public static final String CUSTOMER_GET_BY_ID_ERROR = "Error getting customer by ID {}: {}";

    public static final String CUSTOMER_GET_ALL_START = "Processing get all customers request";
    public static final String CUSTOMER_GET_ALL_SUCCESS = "Customer service successfully retrieved all customers";
    public static final String CUSTOMER_GET_ALL_ERROR = "Error getting all customers: {}";

    public static final String CUSTOMER_UPDATE_START = "Processing update customer request for ID: {}";
    public static final String CUSTOMER_UPDATE_SUCCESS = "Customer service successfully updated customer: {}";
    public static final String CUSTOMER_UPDATE_ERROR = "Error updating customer {}: {}";

    public static final String CUSTOMER_DELETE_START = "Processing delete customer request for ID: {}";
    public static final String CUSTOMER_DELETE_SUCCESS = "Customer service successfully deleted customer: {}";
    public static final String CUSTOMER_DELETE_ERROR = "Error deleting customer {}: {}";

    // Account Service Messages
    public static final String ACCOUNT_CREATE_START = "Processing create account request for customer: {}";
    public static final String ACCOUNT_CREATE_SUCCESS = "Account service successfully created account with ID: {}";
    public static final String ACCOUNT_CREATE_ERROR = "Error creating account: {}";

    public static final String ACCOUNT_GET_BY_ID_START = "Processing get account request for ID: {}";
    public static final String ACCOUNT_GET_BY_ID_SUCCESS = "Account service successfully retrieved account: {}";
    public static final String ACCOUNT_GET_BY_ID_ERROR = "Error getting account by ID {}: {}";

    public static final String ACCOUNT_GET_BY_NUMBER_START = "Processing get account request for number: {}";
    public static final String ACCOUNT_GET_BY_NUMBER_SUCCESS = "Account service successfully retrieved account by number: {}";
    public static final String ACCOUNT_GET_BY_NUMBER_ERROR = "Error getting account by number {}: {}";

    public static final String ACCOUNT_GET_ALL_START = "Processing get all accounts request";
    public static final String ACCOUNT_GET_ALL_SUCCESS = "Account service successfully retrieved all accounts";
    public static final String ACCOUNT_GET_ALL_ERROR = "Error getting all accounts: {}";

    public static final String ACCOUNT_GET_BY_CUSTOMER_START = "Processing get accounts request for customer: {}";
    public static final String ACCOUNT_GET_BY_CUSTOMER_SUCCESS = "Account service successfully retrieved accounts for customer: {}";
    public static final String ACCOUNT_GET_BY_CUSTOMER_ERROR = "Error getting accounts for customer {}: {}";

    public static final String ACCOUNT_UPDATE_START = "Processing update account request for ID: {}";
    public static final String ACCOUNT_UPDATE_SUCCESS = "Account service successfully updated account: {}";
    public static final String ACCOUNT_UPDATE_ERROR = "Error updating account {}: {}";

    public static final String ACCOUNT_UPDATE_BALANCE_START = "Processing update account balance request for ID: {} with amount: {}";
    public static final String ACCOUNT_UPDATE_BALANCE_SUCCESS = "Account service successfully updated balance for account: {}";
    public static final String ACCOUNT_UPDATE_BALANCE_ERROR = "Error updating account balance {}: {}";

    public static final String ACCOUNT_DELETE_START = "Processing delete account request for ID: {}";
    public static final String ACCOUNT_DELETE_SUCCESS = "Account service successfully deleted account: {}";
    public static final String ACCOUNT_DELETE_ERROR = "Error deleting account {}: {}";

    // Transaction Service Messages
    public static final String TRANSACTION_CREATE_START = "Processing create transaction request for customer: {} and account: {}";
    public static final String TRANSACTION_CREATE_SUCCESS = "Transaction service successfully created transaction with ID: {}";
    public static final String TRANSACTION_CREATE_ERROR = "Error creating transaction: {}";

    public static final String TRANSACTION_GET_BY_ID_START = "Processing get transaction request for ID: {}";
    public static final String TRANSACTION_GET_BY_ID_SUCCESS = "Transaction service successfully retrieved transaction: {}";
    public static final String TRANSACTION_GET_BY_ID_ERROR = "Error getting transaction by ID {}: {}";

    public static final String TRANSACTION_UPDATE_START = "Processing update transaction request for ID: {}";
    public static final String TRANSACTION_UPDATE_SUCCESS = "Transaction service successfully updated transaction: {}";
    public static final String TRANSACTION_UPDATE_ERROR = "Error updating transaction {}: {}";

    public static final String TRANSACTION_DELETE_START = "Processing delete transaction request for ID: {}";
    public static final String TRANSACTION_DELETE_SUCCESS = "Transaction service successfully deleted transaction: {}";
    public static final String TRANSACTION_DELETE_ERROR = "Error deleting transaction {}: {}";

    public static final String TRANSACTION_GET_BY_CUSTOMER_ACCOUNT_START = "Processing get transactions request for customer: {} and account: {} between {} and {}";
    public static final String TRANSACTION_GET_BY_CUSTOMER_ACCOUNT_SUCCESS = "Transaction service successfully retrieved transactions for customer: {} and account: {}";
    public static final String TRANSACTION_GET_BY_CUSTOMER_ACCOUNT_ERROR = "Error getting transactions for customer {} and account {}: {}";

    public static final String TRANSACTION_GET_ALL_ACTIVE_START = "Processing get all active transactions request";
    public static final String TRANSACTION_GET_ALL_ACTIVE_SUCCESS = "Transaction service successfully retrieved all active transactions";
    public static final String TRANSACTION_GET_ALL_ACTIVE_ERROR = "Error getting all active transactions: {}";
}
