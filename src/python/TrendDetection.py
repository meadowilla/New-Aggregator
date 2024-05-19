import csv
import numpy as np
from nltk.corpus import stopwords
from nltk.tokenize import word_tokenize
from sklearn.feature_extraction.text import TfidfVectorizer
from nltk.stem import PorterStemmer

# Text acquisition: identifies and stores documents
def read_csv(file_path):
    key_documents = []
    with open(file_path, 'r', encoding='utf-8', errors='ignore') as input_file:
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


def search_key_ranking(key, key_dict, rows, documents):
    output_documents = []
    rank_indices = tfidf_ranking(key, documents)
    for idx in rank_indices:
        for k in key_dict[idx]:
            if k in key:
                output_documents.append(rows[idx])
                break
    return len(output_documents)


def main():
    file_path = 'articles_2024.csv'
    key_documents, rows = read_csv(file_path)
    
    keywords = [
    "NFT", "Ethereum", "Bitcoin", "Fintech", "Smart Contract",
    "Decentralized","Cryptocurrency", "Blockchain", 
     "Tokenization","Distributed Ledger",
     "Consensus", "Immutable",
    "DeFi", "Mining", "Wallet", "Hash Function", "Digital Asset",
    "Stablecoin", "Privacy Coin", "DAO", "Interoperability",
    "Oracles", "Web3", "Layer 2 Solutions", "Halving",
    "Proof of Work (PoW)", "Proof of Stake (PoS)", "Cross-Chain",
    ] # List of blockchain keywords
    
    year = "2024"  # Specific year for search
    
    keyword_results = {}  # Dictionary to store keyword and search result counts
    
    for keyword in keywords:
        count = search_key_ranking(keyword, create_key_dict(key_documents), rows, key_documents)
        keyword_results[keyword] = count
    
    # Sort the keyword results based on search result counts
    sorted_results = sorted(keyword_results.items(), key=lambda x: x[1], reverse=True)
    
    print("Top 3 blockchain keywords in", year, "based on search results:")
    for keyword, count in sorted_results[:3]:
        print(f"{keyword}: {count} search results")

if __name__ == "__main__":
    main()
