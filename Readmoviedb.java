package cps842project;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

public class Readmoviedb {
	

	public static void parseAtCommas(HashMap<String,String> movieTitleGenre,HashMap<String,Double> movieTitleRating, String line)
	{

		boolean quotes = false;
		List<String> titles = new ArrayList<String>();
		int startPos = 0;
		
		for (int currentPos = 0; currentPos < line.length(); currentPos++)
		{
			/* 
			 * If the line starts with a quote, then flip the boolean value of quotes
			 */
			if (line.charAt(currentPos) == '\"')
			{
				quotes = !quotes;
			}
			
			/* If the current character is a comma and quotes is false (means current position is not in quotes),
			 * add the substring from the starting position to current position, then change the startPos to 
			 * the next position
			 */
			else if (line.charAt(currentPos) == ',' && quotes == false)
			{

				titles.add(line.substring(startPos,currentPos));
				startPos = currentPos+1;
				
			}
			
			
		}
		
		
		String genre = titles.get(1);		
		List<String> genreDisplay = new ArrayList<String>();
		boolean genreQuotes = false;
		
		int startPosGenre = 0;
		
		
		/* Gets the genre from the titles string array 
		 */
		if (!(genre.equals("[]")))
		{
			for (int currentGenrePos = 0; currentGenrePos < genre.length(); currentGenrePos++)
			{
				
				/* Genre's start with an upper case, so checks to see if the current character is an upper case
				 * and if the genre word is in quotes, change the start position to the beginning of the upper 
				 * case letter and flip then boolean value
				 */
				if (Character.isUpperCase(genre.charAt(currentGenrePos)) == true && genreQuotes == false)
				{
				startPosGenre = currentGenrePos;
				genreQuotes = !genreQuotes;
				}
					
				if (genre.charAt(currentGenrePos) == '\"' && genreQuotes == true)
				{
					
					genreQuotes = !genreQuotes;
					//System.out.println(genre.substring(startPosGenre, currentGenrePos));
					genreDisplay.add(genre.substring(startPosGenre, currentGenrePos));
				}
				

				
			}
			
			
			
		}
		
		/* If the genre is empty, then "none" will be added to the genre instead of empty space 
		 */
		else if (genre.equals("[]"))
		{
			genreDisplay.add("none");
			
		}
		//System.out.println(titles.get(18));
		String rating = titles.get(18);
		double numRating = Double.parseDouble(rating);		
		
		
		movieTitleGenre.put(titles.get(6),genreDisplay.get(0));		
		movieTitleRating.put(titles.get(6),numRating);
	}

	public static Map<String, String> listMovies(HashMap<String,Double> movieTitleRating, BufferedReader bufferReader) throws IOException
	{
		StringBuffer movieStringBuffer = new StringBuffer();
		String line;
		String title;
		String[] titleArray;
		
		//Skips the first line because first line of file is not a data entry point
		line = bufferReader.readLine();
		
		HashMap<String,String> movieTitleGenreRatingIn = new HashMap<String,String>();
		
		while ((line = bufferReader.readLine()) != null) 
		{	
			
			/*
			 * Since the first character of a movie entry is always a digit, run through the if statements to check if the first character
			 * is a digit then proceed accordingly.
			 */
			boolean testDigit = Character.isDigit(line.charAt(0));
			
			// If movieStringBuffer previously is set to 0 or first line of file, just add line to string buffer
			if (testDigit == true && movieStringBuffer.length() == 0)
			{
				movieStringBuffer.append(line);
			}
			
			
			/* 
			 * If the current line starts with a number and previous line is still in movieStringBuffer, 
			 * then parse previous line in movieStringBuffer and then reset movieStringBuffer to 0.
			 * Then add current line to movieStringBuffer
			 */
			else if (testDigit == true && !(movieStringBuffer.length() == 0))
			{
				title = movieStringBuffer.toString();
				parseAtCommas(movieTitleGenreRatingIn,movieTitleRating, title);
				movieStringBuffer.setLength(0);
				movieStringBuffer.append(line);
			}
			
			/*
			 * If current line does not start with a digit, it means the previous line is a multiple lined movie data.
			 * Then add current line to the movieStringBuffer and parse, and reset the movieStringBuffer to 0.
			 */
			else if (testDigit == false)
			{
				
				movieStringBuffer.append(line);
				title = movieStringBuffer.toString();
				parseAtCommas(movieTitleGenreRatingIn, movieTitleRating, title);
				//System.out.println(title);
				movieStringBuffer.setLength(0);
			}
		}// while loop readline not null
		
		/*
		 * This is for the last line of the file and checks to see if there is still a line left inside movieStringBuffer.
		 */
		if (!(movieStringBuffer.length() == 0))
		{
			title = movieStringBuffer.toString();
			parseAtCommas(movieTitleGenreRatingIn, movieTitleRating,  title);
			movieStringBuffer.setLength(0);
		}
		
		Map<String,String> movieTitleGenreRatingOut = new TreeMap<>(movieTitleGenreRatingIn);
		
		return movieTitleGenreRatingOut;
	}
	
	
	
	
	public static void main(String[] args) throws ClassNotFoundException 
	{

		try 
		{			
			// tester file
			//FileReader text = new FileReader("movies_cut.csv");
			
			// official file
			FileReader text = new FileReader("tmdb_5000_movies.csv");
			BufferedReader bufferedReader = new BufferedReader(text);
			
			HashMap<String,String> movieTitleGenreRating = new HashMap<String,String>();
			HashMap<String,Double> movieTitleRating = new HashMap<String,Double>();
			listMovies(movieTitleRating, bufferedReader);			
			

		} 
			catch (IOException e) 
				{
				e.printStackTrace();
				}
	}
	
}

