# Demo
# Background: simple program customer login and shopping, everytime they shop build up shopping point. 
# High point means more discount, vise versa 
# Incident happen -  lot of traffic hit our web app, customer complain about it. seems small issue at the beginning, however in the end financial department found out lot of no shopping point cutomer take the advantage of high discount.
# Root cause it is Customer Object

public class Customer {
    private static final int MIN_INVOICE_POINT = 500;   # 1 Lowest point to allows paying
    private Id id;  # 2 Unique value that identifies the cstomer
    private Name name;  # 3 Customer first and last name
    private Order order;    # 4 Contain all items displayed in the shopping cart
    private CreditScore creditScore;    # 5 Service that computes the current creditScore

    public synchronized Id getId() {
        return id;
        }
        public synchronized void setId(final Id id) {
        this.id = id;
        }
        public synchronized Name getName() {
        return name;
        }
        public synchronized void setName(Name name) {
        this.name = name;
        }
        public synchronized Order getOrder() {
        this.order = OrderService.fetchLatestOrder(id);
        return order;
        }
        public synchronized void setOrder(Order order) {
        this.order = order;
        }
        public synchronized CreditScore getCreditScore() {
        return creditScore;
        }
    
    public synchronized void setCreditScore(creditScore creditScore) {
        this.creditScore = creditScore; # 6 Initialized the credit score field
    }

    public synchronized boolean isAcceptedForInvoicePayment() {
        return creditScore.comput() >
                                    MIN_INVOICE_POINT;  # 7 Determine if the customer is eligible for the payment
    }
}

