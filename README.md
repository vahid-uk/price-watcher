# Price watcher API

This project consists of the following 2 main aspects. 

## Ecommerce content:

As the following service `ProductServiceInMemoryImpl` is activated at start up it reads in 
`resources/data/product.json` which populates an internal hashmap with what is available ecommerce products. 
This service is accessible via `ProductController` where the end user can either look up a given product or update an 
available product with a new price. This is explained using curl examples further down.

## Monitoring products for price change:

The end user can interact with ProductWatchController and send new URL's and desired prices for monitoring, 
they can also remove URL's from watch list, explained using curl examples further down. 

-----------

## About underlying data:

The collection of products came from the following url, there are 100 products in total:

> https://dummyjson.com/products?limit=100&select=title,price

The json was then formatted using:

> https://jsonformatter.org/json-pretty-print

The content during processing of json to object modified to include a URL which is a dummy ecommerce address.

The following dummy url used along existing product id, which can then be used to set up alerts 
`http://amazing-of-amazonia.com/product/{product_id}`

-----------

## Configuration
The following features can be configured in `application.properties`

### Notification period:

The following key `cache.renewal.cron` needs a cron schedule which tells the monitoring system how often to run and 
check for price checks. If not set it will default to every minute.

```properties
## Every minute:
cache.renewal.cron=0 0/1 * * * ?

###########################################
## Every day at midnight:
# cache.renewal.cron=0 0 0 * * ?
## Every day at noon:
# cache.renewal.cron=0 0 12 * * ?
## Every day at noon and midnight:
# cache.renewal.cron=0 0 0,12 * * ?
## Every day at 7 am:
# cache.renewal.cron=0 0 7 * * ?
###########################################
```

### Renewable hashmap type:

You have 2 options when configuring `application.renewable-type` if default is kept it will 
use `service/watch/basic` classes if `history` is set then it will use `service/watch/history`
The difference between both is, in the case of history, each time a product curl command is sent to update a 
product's price that is also been watched, it will add new price change to a hashmap within 
`ProductWatchHistory` object and when notifying user of a price change, it will provide additional breakdown of all the 
price changes. It was put in place to demonstrate how the `RenewableConcurrentHashMap` could be scaled or used in 
different ways. 

```properties
########################################################
# There are two renewable types default and history
# if history is enabled and a given product has price changes,
# it will also capture all price changes and date of change.
########################################################
application.renewable-type=default
#application.renewable-type=history
```

-----------

## Examples of how to interact with API:

## Adding new entries to watch list

Provide a valid url and a desired price for product to either hit or drop below set price. 

```bash
curl --header "Content-Type: application/json" \
  --request POST \
  --data '{"url": "http://amazing-of-amazonia.com/product/3","price": "45.01"}' \
  http://localhost:8080/watch
```


## Deleting entries from watch list

Change to id of given item in watch list to remove an entry from watch list.

```bash
curl --header "Content-Type: application/json" \
  --request DELETE \
  --data '{"url": "http://amazing-of-amazonia.com/product/1"}' \
  http://localhost:8080/delete
```


## Modify existing products prices / title

Change to id price and change price, this will trigger schedule check to
alert if it meets alert criteria.

```bash
curl --header "Content-Type: application/json" \
  --request PUT \
  --data '{"id": "1", "title" : "some new title" , "price": "4.03"}' \
  http://localhost:8080/product
```


## Getting an existent product

Change to id price and change price, this will trigger schedule check to
alert if it meets alert criteria.

```bash
curl --header "Content-Type: application/json" \
  --request GET \
  http://localhost:8080/product/1
```

## Getting a non existent product

Change to id price and change price, this will trigger schedule check to
alert if it meets alert criteria.

```bash
curl --header "Content-Type: application/json" \
  --request GET \
  http://localhost:8080/product/1111
```

-----------

## History feature

This aspect is a little beyond the 4 hours, I decided to include it since it gave an alternative example of how to 
re-use RenewableConcurrentHashMap, making it easier for future developers to expand on the idea.
