#!/usr/bin/env python3
"""
Convert chat text file to pretty CSS-styled markdown
"""
import re
import html
import sys
from pathlib import Path

def convert_chat_to_pretty_markdown(input_file, output_file):
    """Convert chat text to CSS-styled markdown that renders well in browsers"""
    
    # Read input file
    with open(input_file, 'r', encoding='utf-8') as f:
        content = f.read()
    
    # CSS-styled markdown header
    markdown_header = '''# 💬 Coloc Teacher Development Chat

<style>
body {
    max-width: 1000px;
    margin: 0 auto;
    padding: 20px;
    font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
    background: #f8fafc;
    line-height: 1.6;
}

.chat-container {
    margin: 20px 0;
}

.user-message {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
    padding: 15px 20px;
    border-radius: 20px 20px 5px 20px;
    margin: 15px 0 15px 80px;
    box-shadow: 0 3px 10px rgba(102, 126, 234, 0.3);
    position: relative;
    max-width: 75%;
    margin-left: auto;
    margin-right: 20px;
    word-wrap: break-word;
}

.user-message::before {
    content: "🧑‍💻 hinerm";
    position: absolute;
    top: -20px;
    right: 10px;
    font-size: 12px;
    font-weight: 600;
    color: #667eea;
    background: white;
    padding: 2px 8px;
    border-radius: 10px;
}

.assistant-message {
    background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
    color: white;
    padding: 15px 20px;
    border-radius: 20px 20px 20px 5px;
    margin: 15px 80px 15px 0;
    box-shadow: 0 3px 10px rgba(240, 147, 251, 0.3);
    position: relative;
    max-width: 75%;
    word-wrap: break-word;
}

.assistant-message::before {
    content: "🤖 GitHub Copilot";
    position: absolute;
    top: -20px;
    left: 10px;
    font-size: 12px;
    font-weight: 600;
    color: #f093fb;
    background: white;
    padding: 2px 8px;
    border-radius: 10px;
}

.code-block {
    background: #2d3748;
    color: #e2e8f0;
    padding: 16px;
    border-radius: 8px;
    margin: 12px 0;
    font-family: 'Fira Code', 'Consolas', monospace;
    overflow-x: auto;
    border-left: 4px solid #4299e1;
    white-space: pre-wrap;
}

.timestamp {
    text-align: center;
    color: #718096;
    font-size: 12px;
    margin: 30px 0;
    font-style: italic;
}

.section-break {
    height: 20px;
}

h1 {
    text-align: center;
    color: #2d3748;
    margin-bottom: 10px;
}

.subtitle {
    text-align: center;
    color: #718096;
    font-style: italic;
    margin-bottom: 40px;
}
</style>

<div class="subtitle">A conversation between hinerm and GitHub Copilot about building an educational colocalization analysis plugin</div>

<div class="timestamp">🕒 Session started - Exploring Fiji's colocalization tools</div>

'''
    
    # Split into messages using regex
    messages = re.split(r'(?=hinerm:|GitHub Copilot:)', content)
    
    markdown_content = [markdown_header]
    
    for message in messages:
        message = message.strip()
        if not message:
            continue
            
        if message.startswith('hinerm:'):
            content_text = message[7:].strip()
            content_text = html.escape(content_text)
            content_text = content_text.replace('\n', '<br>')
            
            # Handle code blocks
            if '```' in content_text:
                content_text = format_code_blocks(content_text)
            
            markdown_content.append(f'''
<div class="chat-container">
<div class="user-message">
{content_text}
</div>
</div>
''')
            
        elif message.startswith('GitHub Copilot:'):
            content_text = message[15:].strip()
            content_text = html.escape(content_text)
            content_text = content_text.replace('\n', '<br>')
            
            # Handle code blocks
            if '```' in content_text:
                content_text = format_code_blocks(content_text)
                
            markdown_content.append(f'''
<div class="chat-container">
<div class="assistant-message">
{content_text}
</div>
</div>
''')
    
    # Write output
    final_markdown = ''.join(markdown_content)
    with open(output_file, 'w', encoding='utf-8') as f:
        f.write(final_markdown)
    
    return len([m for m in messages if m.strip()])

def format_code_blocks(text):
    """Format code blocks with proper styling"""
    # Handle code blocks more carefully
    text = re.sub(r'```([^`]*?)```', r'</div><div class="code-block">\1</div><div class="user-message" style="background: none; box-shadow: none; padding: 0;">', text, flags=re.DOTALL)
    return text

def main():
    input_file = sys.argv[1] if len(sys.argv) > 1 else "ColocTeacherChat.txt"
    
    if len(sys.argv) > 2:
        output_file = sys.argv[2]
    else:
        # Auto-generate output filename: input.txt -> input-pretty.md
        input_path = Path(input_file)
        output_file = input_path.stem + "-pretty.md"
    
    if not Path(input_file).exists():
        print(f"❌ Error: Input file '{input_file}' not found!")
        return 1
    
    try:
        message_count = convert_chat_to_pretty_markdown(input_file, output_file)
        print(f"✅ Pretty CSS-styled markdown conversion complete!")
        print(f"📄 Original file: {input_file}")
        print(f"🎨 Pretty markdown: {output_file}")
        print(f"💬 Messages processed: {message_count}")
        print(f"🌐 Drag into browser or save as .html for full styling!")
        return 0
    except Exception as e:
        print(f"❌ Error during conversion: {e}")
        return 1

if __name__ == "__main__":
    sys.exit(main())
