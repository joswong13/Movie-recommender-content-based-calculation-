package cps842project;
 


import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.lang.Math;

import javax.swing.*;
import javax.swing.event.*;
 
public class moviegui extends JPanel implements ListSelectionListener 
{
    private JList list;
    private DefaultListModel listModel;
 
    private static final String submitText = "Submit";
    private static final String rateString = "Recommend";
    private JButton rateButton;
    private JTextField movieRating;
    
    int globalRecommendedCounter = 0;
    
    HashMap<String, Integer> userRating = new HashMap<String,Integer>();
    
	 // GLOBAL <movie,genre>
    static Map<String, String> titleGenreMap = new TreeMap<>();
    
	 // GLOBAL <movie,rating>
    static HashMap<String,Double> movieTitleRating = new HashMap<String,Double>();

	// GLOBAL <movie,genre>
    static HashMap<String,String> recommendedMovies = new HashMap<String,String>();

    
    public moviegui(Map<String,String> movieTitleGenreRating) 
    {
        super(new BorderLayout());
        
        listModel = new DefaultListModel();
        
		for (Map.Entry<String, String> entry : movieTitleGenreRating.entrySet())
		{
			String key = entry.getKey();
			String value = entry.getValue();
			listModel.addElement(key + " - " + value);
		}
		
        
        //Create the list and put it in a scroll pane.
        list = new JList(listModel);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setSelectedIndex(0);
        list.addListSelectionListener(this);
        list.setVisibleRowCount(30);
        JScrollPane listScrollPane = new JScrollPane(list);
 
        JButton submitButton = new JButton(submitText);
        SubmitListener SubmitListener = new SubmitListener(submitButton);
        submitButton.setActionCommand(submitText);
        submitButton.addActionListener(SubmitListener);
        submitButton.setEnabled(false);
 
        rateButton = new JButton(rateString);
        rateButton.setActionCommand(rateString);
        rateButton.addActionListener(new RateListener());
 
        movieRating = new JTextField(2);
        
        movieRating.setText("Enter an integer between 1 and 10");
        
        movieRating.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
            	movieRating.setText("");
            }
        });
        movieRating.addActionListener(SubmitListener);
        movieRating.getDocument().addDocumentListener(SubmitListener);
        String name = listModel.getElementAt(list.getSelectedIndex()).toString();
 
        //Create a panel that uses BoxLayout.
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
        buttonPane.add(rateButton);
        buttonPane.add(Box.createHorizontalStrut(5));
        buttonPane.add(new JSeparator(SwingConstants.VERTICAL));
        buttonPane.add(Box.createHorizontalStrut(5));
        buttonPane.add(movieRating);
        buttonPane.add(submitButton);
        buttonPane.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
 
        add(listScrollPane, BorderLayout.CENTER);
        add(buttonPane, BorderLayout.PAGE_END);
    }
 		
	//recommend button listener
    class RateListener implements ActionListener 
    {
        public void actionPerformed(ActionEvent e) 
        {
        	if (globalRecommendedCounter > 4)
        	{
        	// Perform content based calculation here
			
			ArrayList<String> reccomendedMovies = new ArrayList<String>();
			//gets average of users
			double averageRating = 0;
			double movieRating = 0;
			String genre;
			int count = 0;
			for (Map.Entry<String, Integer> entryUser : userRating.entrySet()) {
				String keyUser = entryUser.getKey();
				int valueUser = entryUser.getValue();
			

				//gets average of users
				
				movieRating = movieTitleRating.get(keyUser.trim());
				genre = titleGenreMap.get(keyUser.trim());
			
				averageRating = (valueUser+movieRating)/2;
				averageRating = (double)Math.round(averageRating * 10) / 10;
				
				System.out.println("\n");
				System.out.println("Movie Selected: " + keyUser);				
				System.out.println("We are looking for averageRating: " + averageRating);
				System.out.println("With the genre: " + genre);
				System.out.println();
			
				for (Map.Entry<String, Double> entryDB : movieTitleRating.entrySet()){
					String keyDB = entryDB.getKey();
					double valueDB = entryDB.getValue();
					//same rating
					if (valueDB == averageRating) {
						for (Map.Entry<String, String> entryGenre : titleGenreMap.entrySet()){
							String genreDB = entryGenre.getValue();
							//same genre
							if (genreDB.equals(genre)) {
								recommendedMovies.put(keyDB, genre);				
							}
						}
					}
				}
			}
			createAndShowGUI(recommendedMovies);
			
        	}
        	
        	
        }
    }
 
    //This listener is shared by the text field and the hire button.
    class SubmitListener implements ActionListener, DocumentListener 
    {
        private boolean alreadyEnabled = false;
        private JButton button;
 
        public SubmitListener(JButton button) 
        {
            this.button = button;
        }
 
        //Required by ActionListener.
        public void actionPerformed(ActionEvent e) 
        {
            String numberRating = movieRating.getText();
            
            int userRatingNumber = Integer.parseInt(numberRating);
        	
            int startPos = 0;
        	
        	//Get selection as string
        	String title = (String) list.getSelectedValue();
        	
        	//Remove the genre
        	for (int currentPos = 0; currentPos < title.length(); currentPos++)
        	{
        		if (title.charAt(currentPos) == '-')
        		{
        			title = title.substring(startPos, currentPos);
        			
        		}
        		
        	}
        	
        	// add user rating to map
        	userRating.put(title, userRatingNumber);
        	
        	globalRecommendedCounter += 1;
        	
        	
        	System.out.println(globalRecommendedCounter);
    		
        	for (Map.Entry<String, Integer> entry : userRating.entrySet())
    		{
    			String key = entry.getKey();
    			int value = entry.getValue();
    			
    			System.out.println(key + value);

    		}
        	
        	
            //Reset the text field.
            movieRating.requestFocusInWindow();
            movieRating.setText("");
            }
            
 
        //Required by DocumentListener.
        public void insertUpdate(DocumentEvent e) {
            enableButton();
        }
 
        //Required by DocumentListener.
        public void removeUpdate(DocumentEvent e) {
            handleEmptyTextField(e);
        }
 
        //Required by DocumentListener.
        public void changedUpdate(DocumentEvent e) {
            if (!handleEmptyTextField(e)) {
                enableButton();
            }
        }
 
        private void enableButton() {
            if (!alreadyEnabled) {
                button.setEnabled(true);
            }
        }
 
        private boolean handleEmptyTextField(DocumentEvent e) {
            if (e.getDocument().getLength() <= 0) {
                button.setEnabled(false);
                alreadyEnabled = false;
                return true;
            }
            return false;
        }
    }
 
    //This method is required by ListSelectionListener.
    public void valueChanged(ListSelectionEvent e) 
    {
        if (e.getValueIsAdjusting() == false) 
        {
 
            if (list.getSelectedIndex() == -1) 
            {
            	rateButton.setEnabled(false);
 
            } 
            else 
            {
            	rateButton.setEnabled(true);
            }
        }
    }
 
    /*
     * Create the GUI and show it
     */
    private static void createAndShowGUI(Map<String,String> movieTitleGenreRating) 
    {
        //Create and set up the window.
        JFrame frame = new JFrame("Movie Titles");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 
        //Create and set up the content pane.
        JComponent newContentPane = new moviegui(movieTitleGenreRating);
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);
 
        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }
 
    public static void main(String[] args) throws IOException {
    	
    	/*
    	 * Read in movie DB
    	 * 
    	 */
		FileReader text = new FileReader("tmdb_5000_movies.csv");
		BufferedReader bufferedReader = new BufferedReader(text);
		
		/*
		 * Get the movieTitleGenre
		 * and send to the list generating GUI function
		 */
		
		Readmoviedb readmoviedb = new Readmoviedb();
		
		
		//global tree map of title and genre = titleGenreMap
		//global hashmap of title and rating = movieTitleRating
		titleGenreMap = readmoviedb.listMovies(movieTitleRating, bufferedReader);	
		
		
		
		// Testing results
		/*
		for (Map.Entry<String, Double> entry : movieTitleRating.entrySet())
		{
			String key = entry.getKey();
			double value = entry.getValue();
			System.out.println(key  + " - " + value);
		}
		
    	for (int i = 0; i < movieTitleList.size(); i++)
    	{
    		System.out.println(movieTitleList.get(i));
    		
    	}*/
    	
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() 
            {
                createAndShowGUI(titleGenreMap);
            }
        });
    }
}
