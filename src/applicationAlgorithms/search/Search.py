from applicationAlgorithms.index.Index import *
import numpy as np
from sklearn.feature_extraction.text import TfidfVectorizer

# TF-IDF ranking
def tfidf_ranking(query, documents):
    vectorizer = TfidfVectorizer()
    # Fit and transform the documents into TF-IDF vectors
    tfidf_matrix = vectorizer.fit_transform(documents)
    # Transform the query into a TF-IDF vector
    query_vec = vectorizer.transform([query])
    # Calculate cosine similarity between query vector and document vectors
    cosine_sim = np.dot(tfidf_matrix, query_vec.T).toarray().flatten()
    # Get indices of documents sorted by relevance
    ranked_indices = np.argsort(cosine_sim)[::-1]
    return ranked_indices

# Search keys with ranking
def search_key_ranking(key, key_dict, rows, documents):
    output_documents = []
    rank_indices = tfidf_ranking(key, documents)
    for idx in rank_indices:
        for k in key_dict[idx]:
            if k in key:
                output_documents.append(rows[idx])
                break
    return output_documents

def search_key_year_ranking(key, year, key_dict, rows, documents):
    output_documents = []
    rank_indices = tfidf_ranking(key, documents)
    for idx in rank_indices:
        for k in key_dict[idx]:
            if k in key and rows[idx][6][:4] == year:
                output_documents.append(rows[idx])
                break
    return output_documents

def search_key_year_month_ranking(key, year, month, key_dict, rows, documents):
    output_documents = []
    rank_indices = tfidf_ranking(key, documents)
    for idx in rank_indices:
        for k in key_dict[idx]:
            if k in key and rows[idx][6][:4] == year and rows[idx][6][5:7] == month:
                output_documents.append(rows[idx])
                break
    return output_documents

# Return new csv file
def return_new_csv(output_file, output_documents):
    with open(output_file, "w", newline='', encoding='utf-8') as output_file: # write new information into new cvs file
            csv_writer = csv.writer(output_file)
            for row in output_documents:
                csv_writer.writerow(row)

# Main function
def key_search(search_key, key_documents, rows):
    dict = create_key_dict(key_documents)
    output_documents = search_key_ranking(search_key, dict, rows, key_documents)
    return output_documents

def key_year_search(search_key, year, key_documents, rows):
    dict = create_key_dict(key_documents)
    output_documents = search_key_year_ranking(search_key, year, dict, rows, key_documents)
    return output_documents
def key_year_month_search(search_key, year, month, key_documents, rows):
    if year == "Year":
        return key_search(search_key, key_documents, rows)
    elif month == "Month":
        return key_year_search(search_key, year, key_documents, rows)
    else:
        dict = create_key_dict(key_documents)
        output_documents = search_key_year_month_ranking(search_key, year, month, dict, rows, key_documents)
        return output_documents
if __name__ == "__main__":
    file_path = 'src\\application_data\\data\\data.csv' # Relative Path to your csv file
    key_documents, rows = read_csv(file_path) # Maybe you need to change the idx of rows to align with your csv file

    search_key = "blockchain"
    year = "2024"
    month = "02"
    
    # key_search(search_key, key_documents, rows)
    # key_year_search(search_key, year, key_documents, rows)
    # key_year_month_search(search_key, year, month, key_documents, rows)
    print(len(key_search("blockchain", key_documents, rows)))
    print(len(key_year_search("blockchain", "2024", key_documents, rows)))





