import csv

# categorize based on types
def categorize_data(filename):
    types_set = set()  # Using a set to ensure uniqueness of types
    source_set = set()
    
    with open(filename, 'r', encoding='utf-8', errors='replace') as csvfile:
        reader = csv.DictReader(csvfile)
        for row in reader:
            # print(row)
            type_ = row['Type']
            source_ = row['Source website']
            
            types_set.add(type_)  # Add type to the set
            source_set.add(source_)
            
    return types_set, source_set


filename = 'data/test_year_search.csv'  
types_set, source_set = categorize_data(filename)
print(types_set)
print(source_set)

# This code is runable, but what is its' advantage? Should it help filtering function really?


