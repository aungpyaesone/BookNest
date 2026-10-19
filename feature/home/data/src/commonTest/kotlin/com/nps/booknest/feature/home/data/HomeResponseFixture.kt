package com.nps.booknest.feature.home.data

internal val homeResponseFixture =
    """
{
  "banners": [
    {
      "bannerId": "home-reading",
      "title": "Good Books.\nGreat moments.",
      "subtitle": "Buy, rent and read your favorite books anytime, anywhere.",
      "imageUrl": "/images/home/reading-banner.svg",
      "buttonText": "Explore Now",
      "action": "OPEN_EXPLORE"
    }
  ],
  "categories": [
    {
      "categoryId": 1,
      "name": "Fiction",
      "slug": "fiction",
      "iconKey": "book-open"
    },
    {
      "categoryId": 2,
      "name": "Education",
      "slug": "education",
      "iconKey": "graduation-cap"
    },
    {
      "categoryId": 3,
      "name": "Business",
      "slug": "business",
      "iconKey": "briefcase"
    },
    {
      "categoryId": 4,
      "name": "Self-help",
      "slug": "self-help",
      "iconKey": "sprout"
    }
  ],
  "popularBooks": [
    {
      "bookId": 1,
      "title": "Atomic Habits",
      "coverImageUrl": "https://booknest-api-3jl3.onrender.com/images/home/atomic-habits.svg",
      "authors": [
        {
          "authorId": 1,
          "authorName": "James Clear"
        }
      ],
      "purchasePrice": 1200.0,
      "rentalPrice": 300.0,
      "currencyCode": "JPY",
      "rentalDays": 30,
      "pageCount": 100,
      "languageCode": "en",
      "fileFormat": "PDF",
      "premiumEligible": false,
      "isDemo": true
    },
    {
      "bookId": 2,
      "title": "The Psychology of Money",
      "coverImageUrl": "https://booknest-api-3jl3.onrender.com/images/home/psychology-of-money.svg",
      "authors": [
        {
          "authorId": 2,
          "authorName": "Morgan Housel"
        }
      ],
      "purchasePrice": 1000.0,
      "rentalPrice": 250.0,
      "currencyCode": "JPY",
      "rentalDays": 30,
      "pageCount": 100,
      "languageCode": "en",
      "fileFormat": "PDF",
      "premiumEligible": false,
      "isDemo": true
    },
    {
      "bookId": 3,
      "title": "Rich Dad Poor Dad",
      "coverImageUrl": "https://booknest-api-3jl3.onrender.com/images/home/rich-dad-poor-dad.svg",
      "authors": [
        {
          "authorId": 3,
          "authorName": "Robert T. Kiyosaki"
        }
      ],
      "purchasePrice": 900.0,
      "rentalPrice": 200.0,
      "currencyCode": "JPY",
      "rentalDays": 30,
      "pageCount": 100,
      "languageCode": "en",
      "fileFormat": "PDF",
      "premiumEligible": false,
      "isDemo": true
    },
    {
      "bookId": 4,
      "title": "Ikigai",
      "coverImageUrl": "https://booknest-api-3jl3.onrender.com/images/home/ikigai.svg",
      "authors": [
        {
          "authorId": 4,
          "authorName": "Hector Garcia"
        }
      ],
      "purchasePrice": 800.0,
      "rentalPrice": 200.0,
      "currencyCode": "JPY",
      "rentalDays": 30,
      "pageCount": 100,
      "languageCode": "en",
      "fileFormat": "PDF",
      "premiumEligible": false,
      "isDemo": true
    }
  ]
}
    """.trimIndent()
