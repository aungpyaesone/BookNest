package com.nps.booknest.feature.explore.data

internal val catalogResponseFixture =
    """
{
  "page": 1,
  "pageSize": 20,
  "total": 6,
  "items": [
    {
      "book": {
        "bookId": 9007199254740993,
        "title": "Where the Crawdads Sing",
        "description": "DEMO catalog metadata for Figma integration. Prices and page counts are sample values. No licensed ebook content is included.",
        "coverImageUrl": "/images/cover.svg",
        "isbn": null,
        "publisherName": null,
        "publishedDate": null,
        "languageCode": "en",
        "pageCount": 368,
        "purchasePrice": 12.75,
        "rentalPrice": 300.0,
        "currencyCode": "USD",
        "rentalDays": 30,
        "previewUrl": null,
        "fileFormat": "PDF",
        "fileSizeBytes": null,
        "contentVersion": 1,
        "premiumEligible": false,
        "status": "PUBLISHED",
        "createdAt": "2026-10-17T08:01:05.972172+00:00",
        "updatedAt": "2026-10-17T08:01:05.972172+00:00"
      },
      "authors": [
        {
          "authorId": 9007199254740994,
          "authorName": "One"
        },
        {
          "authorId": 2,
          "authorName": "Two"
        }
      ],
      "categories": [
        {
          "categoryId": 1,
          "name": "Fiction",
          "slug": "fiction",
          "isPrimary": true
        },
        {
          "categoryId": 18,
          "name": "Mystery",
          "slug": "fiction-mystery",
          "isPrimary": false
        }
      ],
      "rating": null,
      "reviewCount": 0,
      "isDemo": true
    },
    {
      "book": {
        "bookId": 10,
        "title": "The Book Thief",
        "description": "DEMO catalog metadata for Figma integration. Prices and page counts are sample values. No licensed ebook content is included.",
        "coverImageUrl": "https://booknest-api-3jl3.onrender.com/images/figma/catalog-placeholder.svg",
        "isbn": null,
        "publisherName": null,
        "publishedDate": null,
        "languageCode": "en",
        "pageCount": 552,
        "purchasePrice": 1100.0,
        "rentalPrice": 300.0,
        "currencyCode": "JPY",
        "rentalDays": 30,
        "previewUrl": null,
        "fileFormat": "PDF",
        "fileSizeBytes": null,
        "contentVersion": 1,
        "premiumEligible": false,
        "status": "PUBLISHED",
        "createdAt": "2026-10-17T08:01:05.972172+00:00",
        "updatedAt": "2026-10-17T08:01:05.972172+00:00"
      },
      "authors": [
        {
          "authorId": 30,
          "authorName": "Markus Zusak"
        }
      ],
      "categories": [
        {
          "categoryId": 1,
          "name": "Fiction",
          "slug": "fiction",
          "isPrimary": true
        },
        {
          "categoryId": 20,
          "name": "Fantasy",
          "slug": "fiction-fantasy",
          "isPrimary": false
        }
      ],
      "rating": null,
      "reviewCount": 0,
      "isDemo": true
    }
  ]
}
    """.trimIndent()

internal val taxonomyResponseFixture =
    """
{
  "categories": [
    {
      "categoryId": 1,
      "parentCategoryId": null,
      "name": "Fiction",
      "slug": "fiction",
      "iconKey": "book-open",
      "bannerImageUrl": null,
      "bookCount": 6
    },
    {
      "categoryId": 20,
      "parentCategoryId": 1,
      "name": "Fantasy",
      "slug": "fiction-fantasy",
      "iconKey": "book-open",
      "bannerImageUrl": null,
      "bookCount": 2
    },
    {
      "categoryId": 24,
      "parentCategoryId": 2,
      "name": "Language",
      "slug": "education-language",
      "iconKey": "book-open",
      "bannerImageUrl": null,
      "bookCount": 0
    },
    {
      "categoryId": 28,
      "parentCategoryId": 3,
      "name": "Management",
      "slug": "business-management",
      "iconKey": "book-open",
      "bannerImageUrl": null,
      "bookCount": 1
    },
    {
      "categoryId": 32,
      "parentCategoryId": 4,
      "name": "Motivation",
      "slug": "self-help-motivation",
      "iconKey": "book-open",
      "bannerImageUrl": null,
      "bookCount": 0
    },
    {
      "categoryId": 2,
      "parentCategoryId": null,
      "name": "Education",
      "slug": "education",
      "iconKey": "graduation-cap",
      "bannerImageUrl": null,
      "bookCount": 5
    },
    {
      "categoryId": 19,
      "parentCategoryId": 1,
      "name": "Romance",
      "slug": "fiction-romance",
      "iconKey": "book-open",
      "bannerImageUrl": null,
      "bookCount": 1
    },
    {
      "categoryId": 23,
      "parentCategoryId": 2,
      "name": "Science",
      "slug": "education-science",
      "iconKey": "book-open",
      "bannerImageUrl": null,
      "bookCount": 1
    },
    {
      "categoryId": 27,
      "parentCategoryId": 3,
      "name": "Marketing",
      "slug": "business-marketing",
      "iconKey": "book-open",
      "bannerImageUrl": null,
      "bookCount": 0
    },
    {
      "categoryId": 31,
      "parentCategoryId": 4,
      "name": "Happiness",
      "slug": "self-help-happiness",
      "iconKey": "book-open",
      "bannerImageUrl": null,
      "bookCount": 1
    },
    {
      "categoryId": 3,
      "parentCategoryId": null,
      "name": "Business",
      "slug": "business",
      "iconKey": "briefcase",
      "bannerImageUrl": null,
      "bookCount": 7
    },
    {
      "categoryId": 18,
      "parentCategoryId": 1,
      "name": "Mystery",
      "slug": "fiction-mystery",
      "iconKey": "book-open",
      "bannerImageUrl": null,
      "bookCount": 1
    },
    {
      "categoryId": 22,
      "parentCategoryId": 2,
      "name": "History",
      "slug": "education-history",
      "iconKey": "book-open",
      "bannerImageUrl": null,
      "bookCount": 0
    },
    {
      "categoryId": 26,
      "parentCategoryId": 3,
      "name": "Finance",
      "slug": "business-finance",
      "iconKey": "book-open",
      "bannerImageUrl": null,
      "bookCount": 2
    },
    {
      "categoryId": 30,
      "parentCategoryId": 4,
      "name": "Productivity",
      "slug": "self-help-productivity",
      "iconKey": "book-open",
      "bannerImageUrl": null,
      "bookCount": 1
    },
    {
      "categoryId": 4,
      "parentCategoryId": null,
      "name": "Self-help",
      "slug": "self-help",
      "iconKey": "sprout",
      "bannerImageUrl": null,
      "bookCount": 8
    },
    {
      "categoryId": 17,
      "parentCategoryId": 1,
      "name": "Science Fiction",
      "slug": "fiction-science-fiction",
      "iconKey": "book-open",
      "bannerImageUrl": null,
      "bookCount": 2
    },
    {
      "categoryId": 21,
      "parentCategoryId": 2,
      "name": "Technology",
      "slug": "education-technology",
      "iconKey": "book-open",
      "bannerImageUrl": null,
      "bookCount": 1
    },
    {
      "categoryId": 25,
      "parentCategoryId": 3,
      "name": "Career",
      "slug": "business-career",
      "iconKey": "book-open",
      "bannerImageUrl": null,
      "bookCount": 1
    },
    {
      "categoryId": 29,
      "parentCategoryId": 4,
      "name": "Mental Health",
      "slug": "self-help-mental-health",
      "iconKey": "book-open",
      "bannerImageUrl": null,
      "bookCount": 0
    },
    {
      "categoryId": 5,
      "parentCategoryId": null,
      "name": "Biography",
      "slug": "biography",
      "iconKey": "book-open",
      "bannerImageUrl": null,
      "bookCount": 1
    },
    {
      "categoryId": 6,
      "parentCategoryId": null,
      "name": "History",
      "slug": "history",
      "iconKey": "book-open",
      "bannerImageUrl": null,
      "bookCount": 1
    },
    {
      "categoryId": 7,
      "parentCategoryId": null,
      "name": "Science",
      "slug": "science",
      "iconKey": "book-open",
      "bannerImageUrl": null,
      "bookCount": 0
    },
    {
      "categoryId": 8,
      "parentCategoryId": null,
      "name": "Technology",
      "slug": "technology",
      "iconKey": "book-open",
      "bannerImageUrl": null,
      "bookCount": 0
    },
    {
      "categoryId": 9,
      "parentCategoryId": null,
      "name": "Art & Culture",
      "slug": "art-culture",
      "iconKey": "book-open",
      "bannerImageUrl": null,
      "bookCount": 1
    },
    {
      "categoryId": 10,
      "parentCategoryId": null,
      "name": "Comics",
      "slug": "comics",
      "iconKey": "book-open",
      "bannerImageUrl": null,
      "bookCount": 0
    },
    {
      "categoryId": 11,
      "parentCategoryId": null,
      "name": "Travel",
      "slug": "travel",
      "iconKey": "book-open",
      "bannerImageUrl": null,
      "bookCount": 1
    },
    {
      "categoryId": 12,
      "parentCategoryId": null,
      "name": "Religion",
      "slug": "religion",
      "iconKey": "book-open",
      "bannerImageUrl": null,
      "bookCount": 0
    },
    {
      "categoryId": 13,
      "parentCategoryId": null,
      "name": "Children",
      "slug": "children",
      "iconKey": "book-open",
      "bannerImageUrl": null,
      "bookCount": 0
    },
    {
      "categoryId": 14,
      "parentCategoryId": null,
      "name": "Martial",
      "slug": "martial",
      "iconKey": "book-open",
      "bannerImageUrl": null,
      "bookCount": 0
    },
    {
      "categoryId": 15,
      "parentCategoryId": null,
      "name": "Health",
      "slug": "health",
      "iconKey": "book-open",
      "bannerImageUrl": null,
      "bookCount": 0
    },
    {
      "categoryId": 16,
      "parentCategoryId": null,
      "name": "Cooking",
      "slug": "cooking",
      "iconKey": "book-open",
      "bannerImageUrl": null,
      "bookCount": 1
    }
  ]
}
    """.trimIndent()
