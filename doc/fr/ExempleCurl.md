# Divers exemples de requêtes

## Ajout d'un paiement valide

```
curl --location --request POST 'http://localhost:8080/helloasso/payment' \
--header 'Content-Type: application/json' \
--data-raw '{"data": {"payer": {"dateOfBirth": "1971-01-28T00:00:00+01:00", "email": "mail@mail.fr", "address": "2 Rue Republique", "city": "VILLEURBANNE", "zipCode": "69100", "country": "FRA", "firstName": "Robert", "lastName": "Petit"}, "order": {"id": 18124903, "date": "2020-11-14T11:13:08.2972161+00:00", "formSlug": "asso-form-slug", "formType": "PaymentForm", "organizationSlug": "asso-slug"}, "items": [{"shareAmount": 150, "shareItemAmount": 150, "id": 18124903, "amount": 150, "type": "Payment", "state": "Processed"}], "cashOutState": "Transfered", "paymentReceiptUrl": "https://www.helloasso.com/associations/receipt", "id": 9307923, "amount": 1500, "date": "2020-11-14T11:13:11.6878813+00:00", "paymentMeans": "Card", "state": "Authorized"}, "eventType": "Payment"}'
```
