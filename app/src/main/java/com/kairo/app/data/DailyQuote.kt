package com.kairo.app.data

/**
 * Data class representing a daily motivational quote
 */
data class DailyQuote(
    val text: String,
    val author: String
)

/**
 * Repository for managing daily quotes
 */
object QuoteRepository {
    
    private val quotes = listOf(
        DailyQuote("The way to get started is to quit talking and begin doing.", "Walt Disney"),
        DailyQuote("Don't be pushed around by the fears in your mind. Be led by the dreams in your heart.", "Roy T. Bennett"),
        DailyQuote("The future belongs to those who believe in the beauty of their dreams.", "Eleanor Roosevelt"),
        DailyQuote("It is during our darkest moments that we must focus to see the light.", "Aristotle"),
        DailyQuote("The only way to do great work is to love what you do.", "Steve Jobs"),
        DailyQuote("If you can dream it, you can do it.", "Walt Disney"),
        DailyQuote("Success is not final, failure is not fatal: it is the courage to continue that counts.", "Winston Churchill"),
        DailyQuote("The way to get started is to quit talking and begin doing.", "Walt Disney"),
        DailyQuote("Life is what happens to you while you're busy making other plans.", "John Lennon"),
        DailyQuote("The future depends on what you do today.", "Mahatma Gandhi"),
        DailyQuote("It always seems impossible until it's done.", "Nelson Mandela"),
        DailyQuote("Don't watch the clock; do what it does. Keep going.", "Sam Levenson"),
        DailyQuote("The only impossible journey is the one you never begin.", "Tony Robbins"),
        DailyQuote("In the middle of difficulty lies opportunity.", "Albert Einstein"),
        DailyQuote("Success is walking from failure to failure with no loss of enthusiasm.", "Winston Churchill"),
        DailyQuote("The secret of getting ahead is getting started.", "Mark Twain"),
        DailyQuote("You don't have to be great to get started, but you have to get started to be great.", "Les Brown"),
        DailyQuote("The best time to plant a tree was 20 years ago. The second best time is now.", "Chinese Proverb"),
        DailyQuote("Your limitation—it's only your imagination.", "Unknown"),
        DailyQuote("Great things never come from comfort zones.", "Unknown"),
        DailyQuote("Dream it. Wish it. Do it.", "Unknown"),
        DailyQuote("Success doesn't just find you. You have to go out and get it.", "Unknown"),
        DailyQuote("The harder you work for something, the greater you'll feel when you achieve it.", "Unknown"),
        DailyQuote("Dream bigger. Do bigger.", "Unknown"),
        DailyQuote("Don't stop when you're tired. Stop when you're done.", "Unknown"),
        DailyQuote("Wake up with determination. Go to bed with satisfaction.", "Unknown"),
        DailyQuote("Do something today that your future self will thank you for.", "Unknown"),
        DailyQuote("Little things make big days.", "Unknown"),
        DailyQuote("It's going to be hard, but hard does not mean impossible.", "Unknown"),
        DailyQuote("Don't wait for opportunity. Create it.", "Unknown")
    )
    
    /**
     * Get a quote based on the current day of the year
     */
    fun getQuoteForToday(): DailyQuote {
        val dayOfYear = java.time.LocalDate.now().dayOfYear
        return quotes[dayOfYear % quotes.size]
    }
    
    /**
     * Get a random quote
     */
    fun getRandomQuote(): DailyQuote {
        return quotes.random()
    }
}
