import csv
import numpy as np
from nltk.corpus import stopwords
from nltk.tokenize import word_tokenize
from nltk.stem import PorterStemmer
from sklearn.feature_extraction.text import TfidfVectorizer

# Text acquisition: identifies and stores documents
def read_csv(file_path):
    key_documents = []
    with open(file_path, 'r', encoding='utf-8') as input_file:
        csv_reader = csv.reader(input_file)
        rows = list(csv_reader)
        for row in rows:
            key_documents.append(row[3] + " " + row[4] + " " + row[5]) # keywords based on the title, description (content) and author
    return key_documents, rows 

# Text transformation: transforms documents into index terms or features
def preprocess_text(text):
    stop_words = set(stopwords.words('english'))
    stemmer = PorterStemmer()
    # Tokenize the text : process of splitting a text into individual words
    words = word_tokenize(text)
    # Remove stopwords and perform stemming - reduce words to their root
    filtered_words = [stemmer.stem(word.lower()) for word in words if word.lower() not in stop_words] # check word in stopwords or not
    return filtered_words

# Index creation: takes index terms created by text transformations and create data structures to support fast searching
def create_key_dict(documents):
    key_dict = {}
    for doc_id, document in enumerate(documents):
        tokens = preprocess_text(document)
        key_dict[doc_id] = tokens
    return key_dict

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
# ???? Should I limit the number of results here ????
def search_key_ranking(key, key_dict, rows, documents):
    output_documents = []
    rank_indices = tfidf_ranking(key, documents)
    for idx in rank_indices:
        for k in key_dict[idx]:
            if k in key:
                output_documents.append(rows[idx])
                break
    return output_documents

def search_key_year_ranking(key, year, rows, documents):
    output_documents = []
    rank_indices = tfidf_ranking(key, documents)
    for idx in rank_indices:
        if rows[idx][6][:4] == year:
            output_documents.append(rows[idx])
    return output_documents

def search_key_year_month_ranking(key, year, month, rows, documents):
    output_documents = []
    rank_indices = tfidf_ranking(key, documents)
    for idx in rank_indices:
        if rows[idx][6][:4] == year and rows[idx][6][5:7] == month:
            output_documents.append(rows[idx])
    return output_documents

# Return new csv file
def return_new_csv(output_file, output_documents):
    with open(output_file, "w", newline='', encoding='utf-8') as output_file: # write new information into new cvs file
            csv_writer = csv.writer(output_file)
            for row in output_documents:
                csv_writer.writerow(row)

# Main function
def key_search(search_key):
    file_path = 'src\\main\\resources\\test_year_search.csv' # Relative Path to your csv file
    key_documents, rows = read_csv(file_path) # Maybe you need to change the idx of rows to align with your csv file
    dict = create_key_dict(key_documents)
    output_documents = search_key_ranking(search_key, dict, rows, key_documents)
    return output_documents

def key_year_search(search_key, year):
    file_path = 'src\\main\\resources\\test_year_search.csv' # Relative Path to your csv file
    key_documents, rows = read_csv(file_path) # Maybe you need to change the idx of rows to align with your csv file
    output_documents = search_key_year_ranking(search_key, year, rows, key_documents)
    return output_documents
 
def key_year_month_search(search_key, year, month):
    if month =="" and year == "":
        return key_search(search_key)
    elif month == "":
        return key_year_search(search_key, year)
    else:
        file_path = 'src\\main\\resources\\test_year_search.csv' # Relative Path to your csv file
        key_documents, rows = read_csv(file_path) # Maybe you need to change the idx of rows to align with your csv file
        output_documents = search_key_year_month_ranking(search_key, year, month, rows, key_documents)
        return output_documents

if __name__ == "__main__":
    file_path = 'data/test_year_search.csv' # Relative Path to your csv file
    key_documents, rows = read_csv(file_path) # Maybe you need to change the idx of rows to align with your csv file

    search_key = "nft token"
    year = "2023"
    month = "10"
    
    key_search(search_key, key_documents, rows)
    key_year_search(search_key, year, key_documents, rows)
    key_year_month_search(search_key, year, month, key_documents, rows)




