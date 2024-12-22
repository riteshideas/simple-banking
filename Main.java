import java.util.Objects;
import java.util.Scanner;
import java.util.ArrayList;


public class Main {
    static class transaction {
        user from;
        user to;
        double amount;
        String transaction_type;

        transaction(user from, user to, double amount, String transaction_type) {
            this.from = from;
            this.to = to;
            this.amount = amount;
            this.transaction_type = transaction_type;
        }
    }

    static class account_details {
        String name;
        double balance;
        int transaction_len;
        ArrayList<transaction> transactions;
        account_details(String name, double balance, int transaction_len, ArrayList<transaction> transactions) {
            this.name = name;
            this.balance = balance;
            this.transaction_len = transaction_len;
            this.transactions = transactions;
        }
    }

    static class user {
        String name;
        double balance;
        ArrayList<transaction> transactions = new ArrayList<transaction>(1);

        user(String name, double starting_balance) {
            this.name = name;
            this.balance = starting_balance;
        }

        boolean invalid_request_amount(double amt) {
            // should always be greater than 0
            return amt <= 0;
        }

        boolean invalid_transaction(double amt) {
            return invalid_request_amount(amt) || this.balance < amt;
        }


        // return name, balance, no of transactions, last five transactions
        account_details get_account_details() {

            int transaction_size = 0;
            if (this.transactions != null) {
                transaction_size = this.transactions.size();
            }

            int recent_transaction_len = Math.min(this.transactions == null ? 0 : this.transactions.size(), 5);
            ArrayList<transaction> recent_transactions = new ArrayList<transaction>(recent_transaction_len);

            for (int i = 0; i < recent_transaction_len; i++) {
                int index = this.transactions.size() - 1 - i;
                recent_transactions.add(this.transactions.get(index));
            }

            return new account_details(this.name, this.balance, transaction_size, recent_transactions);
        }

        void make_transaction(user to_user, double amount) {
            if (this.invalid_transaction(amount)) {
                System.out.printf("Amount requested to transfer is incorrect (%1$,.2f)%n", amount);
                return;
            }
            this.balance -= amount;
            to_user.balance += amount;

            this.transactions.add(new transaction(this, to_user, amount, "transfer"));
        }

        void deposit(double amount) {
            if (this.invalid_request_amount(amount)) {
                System.out.printf("Amount requested to deposit is negative (%1$,.2f)%n", amount);
                return;
            }

            this.balance += amount;
            this.transactions.add(new transaction(this, this, amount, "deposit"));
        }

        void withdraw(double amount) {
            if (this.invalid_request_amount(amount)) {
                System.out.printf("Amount requested to withdraw is negative (%1$,.2f)%n", amount);
                return;
            }

            this.balance -= amount;
            this.transactions.add(new transaction(this, this, amount, "withdraw"));
        }

    }

    public static void main(String[] args) {
        ArrayList<user> accounts = new ArrayList<user>(0);
        int current_interface = 0; // -1 - exit, 0 - homepage
        int action = -1;
        Scanner scanner = new Scanner(System.in);

        while (current_interface != -1) {
            action = -1;
            switch (current_interface) {
                case 0: // homepage
                    System.out.println("--Homepage--\n 0 - View all accounts\n 1 - Add account\n 2 - View specific account\n 3 - Exit");
                    action = scanner.nextInt();
                    scanner.nextLine();
                    if (action == -1) {
                        break;
                    }
                    else if (action == 0) {
                        current_interface = 2;
                    } else if (action == 1) {
                        current_interface = 1;
                    } else if (action == 2) {
                        current_interface = 3;
                    } else if (action == 3) {
                        current_interface = -1;
                    } else {
                        System.out.println("Command not recognised, please input again");
                        break;
                    }
                    break;

                case 1:
                    System.out.print("--Add Account--\nEnter new account name: ");
                    String name = scanner.nextLine();
                    boolean account_valid = true;

                    for (user usr: accounts) {
                        if (Objects.equals(usr.name, name)) {
                            System.out.println("Account name is already used");
                            account_valid = false;
                            break;
                        }
                    }
                    if (!account_valid) {
                        current_interface = 0;
                        break;
                    }

                    System.out.print("Enter account balance: ");
                    double balance = scanner.nextDouble();
                    accounts.add(new user(name, balance));
                    System.out.println("Account created successfully");

                    current_interface = 0;
                    break;
                case 2: // view all accounts
                    System.out.println("--View all accounts--");
                    for (user usr : accounts) {
                        System.out.printf(" Name: %s , ", usr.name);
                        System.out.printf("balance: %1$,.2f\n", usr.balance);
                    }
                    current_interface = 0;
                    break;
                case 3:
                    System.out.println("--View specific accounts--");
                    for (int i = 0; i < accounts.size(); i++){
                        user usr = accounts.get(i);
                        System.out.printf("[%d]: ", i+1);
                        System.out.printf("Name: %s , ", usr.name);
                        System.out.printf("balance: %1$,.2f\n", usr.balance);
                    }
                    System.out.print("Type the account index to access ");
                    int account_index = scanner.nextInt();
                    account_index -= 1;

                    account_index = Math.min(accounts.size() - 1, Math.max(account_index, 0));
                    System.out.println("--Accessing account--");
                    user viewing_account = accounts.get(account_index);

                    System.out.printf("Name: %s\n", viewing_account.name);
                    System.out.printf("Balance: %1$,.2f\n", viewing_account.balance);

                    account_details account_detail = viewing_account.get_account_details();

                    System.out.printf("Total transactions %d\n", account_detail.transaction_len);
                    for (int i = 0; i < account_detail.transactions.size(); i ++) {

                        transaction transaction_info = account_detail.transactions.get(i);
                        if (Objects.equals(transaction_info.transaction_type, "withdraw")) {
                            System.out.printf("Withdrawn %1$,.2f\n", transaction_info.amount);
                        } else if (Objects.equals(transaction_info.transaction_type, "deposit")) {
                            System.out.printf("Deposited %1$,.2f into account\n", transaction_info.amount);
                        } else if (Objects.equals(transaction_info.transaction_type, "transfer")) {
                            System.out.printf("Transferred %1$,.2f ", transaction_info.amount);
                            System.out.printf("from %s ", transaction_info.from);
                            System.out.printf("to %s\n", transaction_info.to);
                        }
                    }

                    System.out.println("--Options--");
                    System.out.print("[1]: Deposit money\n[2]: Withdraw money\n[3]: Transfer into another account\n");

                    int act = scanner.nextInt();
                    scanner.nextLine();

                    act = Math.min(3, Math.max(1, act));
                    switch (act) {
                        case 1:
                            System.out.print("Deposit amount : ");
                            double deposit_amt = scanner.nextDouble();
                            scanner.nextLine();
                            viewing_account.deposit(deposit_amt);
                            break;
                        case 2:
                            System.out.print("Withdraw amount : ");
                            double withdraw_amt = scanner.nextDouble();
                            scanner.nextLine();

                            if (withdraw_amt > viewing_account.balance) {
                                current_interface = 0;
                                break;
                            }
                            viewing_account.withdraw(withdraw_amt);
                            break;
                        case 3:
                            System.out.println("--View transfer accounts--");
                            if (accounts.size() <= 1) {
                                System.out.println("No other accounts to transfer");
                                current_interface = 0;
                                break;
                            }

                            int i = 0;
                            for (user account : accounts){
                                if (account == viewing_account) {
                                    continue;
                                }
                                System.out.printf("[%d]: ", i+1);
                                System.out.printf("Name: %s , ", account.name);
                                System.out.printf("balance: %1$,.2f\n", account.balance);
                                i ++;
                            }

                            System.out.print("Transfer account index: ");
                            int account_transfer_index = scanner.nextInt();
                            scanner.nextLine();

                            i = 0;
                            user tranfer_account = viewing_account;

                            for (user account : accounts) {
                                if (account == viewing_account) {
                                    continue;
                                }
                                if (i + 1 == account_transfer_index) {
                                    tranfer_account = account;
                                }
                                i ++;
                            }

                            System.out.print("Transfer amount: ");
                            double account_transfer_amt = scanner.nextDouble();
                            scanner.nextLine();

                            viewing_account.make_transaction(tranfer_account, account_transfer_amt);
                            break;
                    }
                    current_interface = 0;
                    break;
            }


        }

    }
}
