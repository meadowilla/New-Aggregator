document.addEventListener('DOMContentLoaded', () => {
    const searchBox = document.getElementById('search-box');
    const suggestionsContainer = document.getElementById('suggestions');

    const suggestions = [
        'Breaking News',
        'World News',
        'Technology',
        'Business',
        'Entertainment',
        'Sports',
        'Science',
        'Health'
    ];

    searchBox.addEventListener('input', () => {
        const query = searchBox.value.toLowerCase();
        suggestionsContainer.innerHTML = '';
        
        if (query) {
            const filteredSuggestions = suggestions.filter(item => item.toLowerCase().includes(query));
            filteredSuggestions.forEach(item => {
                const suggestionItem = document.createElement('div');
                suggestionItem.className = 'suggestion-item';
                suggestionItem.textContent = item;
                suggestionItem.addEventListener('click', () => {
                    searchBox.value = item;
                    suggestionsContainer.style.display = 'none';
                });
                suggestionsContainer.appendChild(suggestionItem);
            });

            if (filteredSuggestions.length > 0) {
                suggestionsContainer.style.display = 'block';
            } else {
                suggestionsContainer.style.display = 'none';
            }
        } else {
            suggestionsContainer.style.display = 'none';
        }

        // Add configuration options
        if (query) {
            addConfigOptions();
        }
    });

    function addConfigOptions() {
        const mustHaveOption = document.createElement('div');
        mustHaveOption.className = 'config-option';
        mustHaveOption.innerHTML = 'Must have word: <input type="text" placeholder="Enter word">';
        
        const notIncludeOption = document.createElement('div');
        notIncludeOption.className = 'config-option';
        notIncludeOption.innerHTML = 'Not include word: <input type="text" placeholder="Enter word">';
        
        suggestionsContainer.appendChild(mustHaveOption);
        suggestionsContainer.appendChild(notIncludeOption);
    }

    document.addEventListener('click', (event) => {
        if (!event.target.matches('#search-box') && !event.target.closest('.suggestions')) {
            suggestionsContainer.style.display = 'none';
        }
    });
});
