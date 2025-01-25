# from flask import Flask, render_template, request, jsonify
# import numpy as np
# from subprocess import CalledProcessError, run
# import whisper

# # libraries for text modification
# from Levenshtein import ratio
# import re
# import json

# app = Flask(__name__, template_folder='templates')

# model = whisper.load_model('base')

# SAMPLE_RATE = 16000


# # converts byte data to what whisper can use (adapted from https://github.com/openai/whisper/blob/main/whisper/audio.py)
# def custom_load_audio(byte_data: bytes, sr=SAMPLE_RATE):
#     cmd = [
#         "ffmpeg",
#         "-nostdin",
#         "-threads", "0",
#         "-i", "-",
#         "-f", "s16le",
#         "-ac", "1",
#         "-acodec", "pcm_s16le",
#         "-ar", str(sr),
#         "-"
#     ]
#     try:
#         out = run(cmd, input=byte_data, capture_output=True, check=True).stdout
#     except CalledProcessError as e:
#         raise RuntimeError(f"Failed to load audio: {e.stderr.decode()}") from e
#     return np.frombuffer(out, np.int16).flatten().astype(np.float32) / 32768.0


# def process_audio(audio):
#     audio = whisper.pad_or_trim(audio)

#     mel = whisper.log_mel_spectrogram(audio).to(model.device)

#     options = whisper.DecodingOptions(fp16=False)
#     result = whisper.decode(model, mel, options)
#     return result.text


# with open('static/json/reference.json', 'r') as json_file:
#     reference_data = json.load(json_file)


# def modify_words(text):  # modifies words so all of them are in the dictionary
#     words = re.findall(r'\b\w+\b', text.lower().strip())
#     filtered_words = [word for word in words if len(word) > 2]
#     modified_words = []
#     for word in filtered_words:
#         modified_word = None
#         for reference_word in reference_data:
#             # Calculate the similarity ratio using Levenshtein distance
#             similarity = ratio(word, reference_word)
#             if similarity >= 0.01:  # Adjust the threshold as needed
#                 modified_word = reference_word
#                 break
#         if not modified_word is None:
#             # we're just removing words that dont match to make it easier (needs to be fixed)
#             modified_words.append(modified_word)
#     return ' '.join(modified_words)


# @app.route("/")
# def home():
#     return render_template('index.html')


# @app.route("/", methods=['POST'])  # check for empty files or no file updated
# def upload_file():
#     f = request.files['file']
#     rawText = process_audio(custom_load_audio(f.read()))
#     modText = modify_words(rawText)
#     return jsonify({'rawText': rawText, 'modText': modText})


# if __name__ == '__main__':
#     app.run(host='0.0.0.0')







###################################################

# from flask import Flask, render_template, request, jsonify
# import re
# import json
# from Levenshtein import ratio

# app = Flask(__name__, template_folder='templates')

# # Load reference data from JSON
# with open('static/json/reference.json', 'r') as json_file:
#     reference_data = json.load(json_file)


# def modify_words(text):  
#     """
#     Modifies words in the input text so all words match the reference dictionary.
#     """
#     words = re.findall(r'\b\w+\b', text.lower().strip())
#     filtered_words = [word for word in words if len(word) > 2]
#     modified_words = []
#     for word in filtered_words:
#         modified_word = None
#         for reference_word in reference_data:
#             # Calculate the similarity ratio using Levenshtein distance
#             similarity = ratio(word, reference_word)
#             if similarity >= 0.8:  # Adjust the threshold as needed
#                 modified_word = reference_word
#                 break
#         if modified_word:
#             modified_words.append(modified_word)
#     return ' '.join(modified_words)


# @app.route("/")
# def home():
#     """
#     Render the home page.
#     """
#     return render_template('index.html')


# @app.route("/process-text", methods=['POST'])
# def process_text():
#     """
#     API route to process a string array and return modified text.
#     """
#     try:
#         # Parse JSON data from the request
#         data = request.get_json()
#         if not data or 'textArray' not in data:
#             return jsonify({'error': 'Invalid input. Provide a textArray in the JSON payload.'}), 400
        
#         text_array = data['textArray']
#         if not isinstance(text_array, list) or not all(isinstance(text, str) for text in text_array):
#             return jsonify({'error': 'textArray must be a list of strings.'}), 400
        
#         processed_texts = [{'rawText': text, 'modText': modify_words(text)} for text in text_array]
#         return jsonify({'processedTexts': processed_texts})
#     except Exception as e:
#         return jsonify({'error': str(e)}), 500


# if __name__ == '__main__':
#     app.run(host='0.0.0.0')






################################third iteration

from flask import Flask, render_template, request, jsonify
from flask_socketio import SocketIO, emit
import re
import json
from Levenshtein import ratio

app = Flask(__name__, template_folder='templates')
app.config['SECRET_KEY'] = 'secret!'  # For SocketIO functionality
socketio = SocketIO(app)  # Initialize SocketIO

# Load reference data from JSON
with open('static/json/reference.json', 'r') as json_file:
    reference_data = json.load(json_file)


def modify_words(text):  
    """
    Modifies words in the input text so all words match the reference dictionary.
    """
    words = re.findall(r'\b\w+\b', text.lower().strip())
    filtered_words = [word for word in words if len(word) > 2]
    modified_words = []
    for word in filtered_words:
        modified_word = None
        for reference_word in reference_data:
            # Calculate the similarity ratio using Levenshtein distance
            similarity = ratio(word, reference_word)
            if similarity >= 0.8:  # Adjust the threshold as needed
                modified_word = reference_word
                break
        if modified_word:
            modified_words.append(modified_word)
    return ' '.join(modified_words)


@app.route("/")
def home():
    """
    Render the home page.
    """
    return render_template('index.html')


@app.route("/process-text", methods=['POST'])
def process_text():
    """
    API route to process a string array and return modified text.
    """
    try:
        # Parse JSON data from the request
        data = request.get_json()
        if not data or 'textArray' not in data:
            return jsonify({'error': 'Invalid input. Provide a textArray in the JSON payload.'}), 400
        
        text_array = data['textArray']
        if not isinstance(text_array, list) or not all(isinstance(text, str) for text in text_array):
            return jsonify({'error': 'textArray must be a list of strings.'}), 400
        
        processed_texts = [{'rawText': text, 'modText': modify_words(text)} for text in text_array]
        
        # Emit a WebSocket event with the processed texts
        socketio.emit('processed_event', {'processedTexts': processed_texts})
        
        return jsonify({'processedTexts': processed_texts})
    except Exception as e:
        return jsonify({'error': str(e)}), 500


# API endpoint to trigger a WebSocket event
@app.route("/trigger", methods=['POST'])
def trigger_event():
    """
    API to trigger a custom WebSocket event.
    """
    try:
        message = request.json.get("message", "")
        if not message:
            return jsonify({'error': 'Invalid input. Provide a "message" in the JSON payload.'}), 400
        
        # Emit the message to the client
        socketio.emit('submit_event', {'message': message})
        return jsonify({'status': 'success', 'message': 'Event triggered'})
    except Exception as e:
        return jsonify({'error': str(e)}), 500


if __name__ == '__main__':
    socketio.run(app, host='0.0.0.0', debug=True)
