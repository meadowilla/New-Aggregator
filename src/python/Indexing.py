import csv
import nltk 
from nltk.corpus import stopwords
from nltk.tokenize import word_tokenize
from nltk.stem import PorterStemmer

# Text acquisition: identifies and stores documents
def read_csv(input_file):
    documents = []
    with open(input_file, 'r', encoding='utf-8') as input_file:
        csv_reader = csv.reader(input_file)
        rows = list(csv_reader)
        for row in rows:
            documents.append(row[0]) # keywords based on the title
    return documents, rows

# Text transformation: transforms documents into index terms or features
def preprocess_text(text):
    stop_words = set(stopwords.words('english'))
    stemmer = PorterStemmer()
    # Tokenize the text
    words = word_tokenize(text)
    # Remove stopwords and perform stemming
    filtered_words = [stemmer.stem(word.lower()) for word in words if word.lower() not in stop_words]
    return filtered_words

# Index creation: takes index terms created by text transformations and create data structures to support fast searching
def create_inverted_index(documents):
    inverted_index = {}
    for doc_id, document in enumerate(documents):
        # Preprocess the document text
        tokens = preprocess_text(document)
        # Update the inverted index
        for token in tokens:
            if token not in inverted_index:
                inverted_index[token] = set()
            inverted_index[token].add(doc_id)
    return inverted_index

# search engine
def search_engine(search_key, inverted_index, rows):
    output_documents = []
    for key in inverted_index.keys():
        if key in search_key:
            output_index = inverted_index[key]
            for idx in output_index:
                output_documents.append(rows[idx])
    return output_documents


def return_new_csv(output_file, output_documents):
    with open(output_file, "w", newline='') as output_file:
            csv_writer = csv.writer(output_file)
            for row in output_documents:
                csv_writer.writerow(row)

def search(search_key):
    file_path = 'src\\main\\resources\\hbr.csv' # Relative Path
    documents, rows = read_csv(file_path)
    inverted_index = create_inverted_index(documents)
    output_documents = search_engine(search_key, inverted_index, rows)
    return output_documents

if __name__ == "__main__":
    search("blockchain technology")

