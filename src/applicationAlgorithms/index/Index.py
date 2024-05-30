import csv
from nltk.corpus import stopwords
from nltk.tokenize import word_tokenize
from nltk.stem import PorterStemmer


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