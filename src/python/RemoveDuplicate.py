import pandas as pd

# Load the CSV file
df = pd.read_csv("E:\\Code\\Spring\\src\\main\\resources\\test_year_search.csv", header=None)

# Define the columns to consider when identifying duplicates
columns_to_consider = [3, 5] 

# Remove duplicates
df.drop_duplicates(subset=columns_to_consider, inplace=True)

# Write the cleaned data back to a new CSV file
df.to_csv('test_year_search_cleaned.csv', index=False, header=False)