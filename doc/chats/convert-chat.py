#!/usr/bin/env python3
"""
Convert chat text file to styled markdown with chat bubbles
"""
import re
import html
import sys
from pathlib import Path

def convert_chat_to_markdown(input_file, output_file):
    """Convert chat text to styled markdown"""
    
    # Read input file
    with open(input_file, 'r', encoding='utf-8') as f:
        content = f.read()
    
    # CSS and header
    markdown_header = '''# 💬 Coloc Teacher Development Chat
*A conversation between hinerm and GitHub Copilot about building an educational colocalization analysis plugin*

---

<style>
.user-message {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
    padding: 12px 16px;
    border-radius: 18px 18px 4px 18px;
    margin: 8px 0 8px 60px;
    box-shadow: 0 2px 8px rgba(102, 126, 234, 0.3);
    position: relative;
    max-width: 80%;
    margin-left: auto;
    margin-right: 20px;
    font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
}

.user-message::before {
    content: "🧑‍💻 hinerm";
    position: absolute;
    top: -18px;
    right: 0;
    font-size: 12px;
    font-weight: 600;
    color: #667eea;
}

.assistant-message {
    background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
    color: white;
    padding: 12px 16px;
    border-radius: 18px 18px 18px 4px;
    margin: 8px 60px 8px 0;
    box-shadow: 0 2px 8px rgba(240, 147, 251, 0.3);
    position: relative;
    max-width: 80%;
    font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
}

.assistant-message::before {
    content: "🤖 GitHub Copilot";
    position: absolute;
    top: -18px;
    left: 0;
    font-size: 12px;
    font-weight: 600;
    color: #f093fb;
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
}

.timestamp {
    text-align: center;
    color: #718096;
    font-size: 12px;
    margin: 20px 0;
    font-style: italic;
}

.section-divider {
    border: 0;
    height: 2px;
    background: linear-gradient(to right, transparent, #e2e8f0, transparent);
    margin: 30px 0;
}
</style>

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
            
            markdown_content.append(f'\n<div class="user-message">\n{content_text}\n</div>\n')
            
        elif message.startswith('GitHub Copilot:'):
            content_text = message[15:].strip()
            content_text = html.escape(content_text)
            content_text = content_text.replace('\n', '<br>')
            
            # Handle code blocks
            if '```' in content_text:
                content_text = format_code_blocks(content_text)
                
            markdown_content.append(f'\n<div class="assistant-message">\n{content_text}\n</div>\n')
    
    # Write output
    final_markdown = ''.join(markdown_content)
    with open(output_file, 'w', encoding='utf-8') as f:
        f.write(final_markdown)
    
    return len([m for m in messages if m.strip()])

def format_code_blocks(text):
    """Format code blocks with proper styling"""
    # Simple code block handling - you can enhance this
    text = re.sub(r'```([^`]+)```', r'<div class="code-block">\1</div>', text, flags=re.DOTALL)
    return text

def main():
    input_file = sys.argv[1] if len(sys.argv) > 1 else "ColocTeacherChat.txt"
    
    if len(sys.argv) > 2:
        output_file = sys.argv[2]
    else:
        # Auto-generate output filename: input.txt -> input.md
        input_path = Path(input_file)
        output_file = input_path.with_suffix('.md').name
    
    if not Path(input_file).exists():
        print(f"❌ Error: Input file '{input_file}' not found!")
        return 1
    
    try:
        message_count = convert_chat_to_markdown(input_file, output_file)
        print(f"✅ Conversion complete!")
        print(f"📄 Original file: {input_file}")
        print(f"🎨 Styled markdown: {output_file}")
        print(f"💬 Messages processed: {message_count}")
        return 0
    except Exception as e:
        print(f"❌ Error during conversion: {e}")
        return 1

if __name__ == "__main__":
    sys.exit(main())
