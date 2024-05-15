import csv

def categorize_csv(filename):
    categorized_data = {}

    with open(filename, 'r', encoding='utf-8', errors='replace') as csvfile:
        reader = csv.DictReader(csvfile)
        for row in reader:
            source_ = row['Source website']
            # Create categories if they don't exist
            if source_ not in categorized_data:
                categorized_data[source_] = []
            categorized_data[source_].append(row)  # Append the entire row
    return categorized_data


def filter_data_by_source(categorized_data, source_):
    if source_ in categorized_data:
        return categorized_data[source_]
    else:
        return []
    
def return_new_csv(categorized_data, output_filename, sources= None):
    with open(output_filename, 'w', newline='', encoding='utf-8') as csvfile:
        fieldnames = ['Link', 'Source website','Website link','Title','Description', 'Author','Published date','Type','Image']  # Adjust fieldnames as needed
        writer = csv.DictWriter(csvfile, fieldnames=fieldnames)
        
        writer.writeheader()
        for source_ in categorized_data:
            if sources is None or source_ in sources:
                for row in categorized_data[source_]:
                    writer.writerow(row)
def main():
    filename = 'data/NewBlockchainData.csv'
    categorized_data = categorize_csv(filename)
    output_filename = 'FilterSource.csv'
    sources = ['Blockworks']
    return_new_csv(categorized_data, output_filename, sources)

if __name__ == "__main__":
    main()
