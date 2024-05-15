import csv
import time

def categorize_csv(filename):
    categorized_data = {}

    with open(filename, 'r', encoding='utf-8', errors='replace') as csvfile:
        reader = csv.DictReader(csvfile)
        for row in reader:
            type_ = row['Type']
            # Create categories if they don't exist
            if type_ not in categorized_data:
                categorized_data[type_] = []
            categorized_data[type_].append(row)  # Append the entire row
    return categorized_data


def filter_data_by_type(categorized_data, type_):
    if type_ in categorized_data:
        return categorized_data[type_]
    else:
        return []
    
def return_new_csv(categorized_data, output_filename, types= None):
    with open(output_filename, 'w', newline='', encoding='utf-8') as csvfile:
        fieldnames = ['Link', 'Source website','Website link','Title','Description', 'Author','Published date','Type','Image']  # Adjust fieldnames as needed
        writer = csv.DictWriter(csvfile, fieldnames=fieldnames)
        
        writer.writeheader()
        for type_ in categorized_data:
            if types is None or type_ in types:
                for row in categorized_data[type_]:
                    writer.writerow(row)
def main():
    st = time.time()
    filename = 'data/NewBlockchainData.csv'
    categorized_data = categorize_csv(filename)
    output_filename = 'output.csv'
    types = ['Digital Article']
    return_new_csv(categorized_data, output_filename, types)
    ed = time.time()
    print("Execution time:", ed - st)
if __name__ == "__main__":
    main()
