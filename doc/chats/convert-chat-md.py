#!/usr/bin/env python3
"""
Convert chat text file to GitHub-friendly markdown
"""
import re
import sys
from pathlib import Path

def convert_chat_to_markdown(input_file, output_file):
    """Convert chat text to GitHub-friendly markdown"""
    
    # Read input file
    with open(input_file, 'r', encoding='utf-8') as f:
        content = f.read()
    
    # GitHub-friendly markdown header
    markdown_header = '''# 💬 Coloc Teacher Development Chat

*A conversation between hinerm and GitHub Copilot about building an educational colocalization analysis plugin for Fiji/ImageJ*

---

**📅 Session Overview**
- **Participants**: hinerm (User) & GitHub Copilot (Assistant)
- **Topic**: Educational colocalization analysis plugin development
- **Outcome**: Full Coloc Teacher plugin with wizard interface

---

'''
    
    # Split into messages using regex
    messages = re.split(r'(?=hinerm:|GitHub Copilot:)', content)
    
    markdown_content = [markdown_header]
    message_count = 0
    
    for message in messages:
        message = message.strip()
        if not message:
            continue
            
        message_count += 1
            
        if message.startswith('hinerm:'):
            content_text = message[7:].strip()
            
            # Clean up the content
            content_text = clean_markdown_content(content_text)
            
            markdown_content.append(f'''
---

### 🧑‍💻 **hinerm**

> {format_as_blockquote(content_text)}

''')
            
        elif message.startswith('GitHub Copilot:'):
            content_text = message[15:].strip()
            
            # Clean up the content
            content_text = clean_markdown_content(content_text)
                
            markdown_content.append(f'''
---

### 🤖 **GitHub Copilot**

> {format_as_blockquote(content_text)}

''')
    
    # Add footer
    footer = f'''
---

**📊 Chat Statistics**
- **Total Messages**: {message_count}
- **Generated**: {Path(output_file).name}
- **Source**: {Path(input_file).name}

*This conversation documents the complete development process of the Coloc Teacher educational plugin for Fiji/ImageJ.*
'''
    
    markdown_content.append(footer)
    
    # Write output
    final_markdown = ''.join(markdown_content)
    with open(output_file, 'w', encoding='utf-8') as f:
        f.write(final_markdown)
    
    return message_count

def clean_markdown_content(text):
    """Clean and format content for GitHub markdown"""
    # Remove excessive whitespace
    text = re.sub(r'\n\s*\n', '\n\n', text)
    text = text.strip()
    
    # Handle code blocks - preserve them but ensure proper formatting
    text = re.sub(r'```(\w*)\n', r'```\1\n', text)
    
    return text

def format_as_blockquote(text):
    """Format text as a blockquote, handling multi-line content"""
    lines = text.split('\n')
    
    # Handle code blocks specially - don't quote them
    in_code_block = False
    result_lines = []
    
    for line in lines:
        if line.strip().startswith('```'):
            in_code_block = not in_code_block
            result_lines.append(line)
        elif in_code_block:
            result_lines.append(line)
        else:
            # Regular text - add blockquote if not empty
            if line.strip():
                if not line.startswith('>'):
                    result_lines.append(f"> {line}")
                else:
                    result_lines.append(line)
            else:
                result_lines.append(">")
    
    return '\n'.join(result_lines)

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
        print(f"✅ GitHub-friendly markdown conversion complete!")
        print(f"📄 Original file: {input_file}")
        print(f"📝 GitHub markdown: {output_file}")
        print(f"💬 Messages processed: {message_count}")
        print(f"🌟 Ready for GitHub viewing!")
        return 0
    except Exception as e:
        print(f"❌ Error during conversion: {e}")
        return 1

if __name__ == "__main__":
    sys.exit(main())
