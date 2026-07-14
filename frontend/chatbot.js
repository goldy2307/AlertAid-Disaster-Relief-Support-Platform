// AlertAid Chatbot - Bulletproof Implementation
// This script creates a chatbot widget that appears on every page immediately

// IMMEDIATE TEST - This should execute as soon as script loads
try {
  console.log('AlertAid Chatbot: SCRIPT FILE LOADED AND EXECUTING');
} catch (e) {
  // If console doesn't exist, create widget anyway
}

(function() {
  'use strict';
  
  // Prevent multiple initializations
  if (window.__alertaid_chatbot_initialized) {
    try {
      console.log('AlertAid Chatbot: Already initialized, skipping');
    } catch (e) {}
    return;
  }
  window.__alertaid_chatbot_initialized = true;
  
  try {
    console.log('AlertAid Chatbot: Script starting...');
  } catch (e) {}
  
  const quickPrompts = [
    { label: 'Report Emergency', question: 'How do I submit an emergency report?' },
    { label: 'Track Report', question: 'How do I track my report status?' },
    { label: 'Donate', question: 'How can I donate to relief efforts?' },
    { label: 'Volunteer', question: 'How do I become a volunteer?' },
    { label: 'Safety Tips', question: 'What are some disaster safety tips?' },
    { label: 'Contact', question: 'How can I contact support?' }
  ];

  const faqAnswers = [
    {
      keywords: ['submit', 'report', 'emergency', 'disaster', 'incident', 'how do i report', 'file report'],
      answer: 'To submit a report: 1) Click on "Report Disaster" from your dashboard, 2) Select the disaster type (flood, earthquake, fire, etc.), 3) Choose severity level, 4) Add your location (GPS or manual), 5) Attach photos if available, 6) Provide a detailed description, 7) Click "Submit". Your report will be reviewed by our team within minutes for critical emergencies.'
    },
    {
      keywords: ['track', 'status', 'progress', 'check report', 'my reports', 'report status'],
      answer: 'To track your report: 1) Go to the "Reports" page from your dashboard, 2) Scroll to "My Reports" section, 3) Each report shows status: Pending Review, Under Verification, Verified, In Progress, Resolved, or Rejected. You\'ll also receive email notifications when status changes. For urgent queries, contact support with your report ID.'
    },
    {
      keywords: ['donate', 'campaign', 'contribute', 'money', 'fund', 'relief', 'help financially'],
      answer: 'To donate: 1) Visit the "Donations" page from your dashboard, 2) Browse active campaigns for different disaster relief efforts, 3) Select a campaign and choose your donation amount, 4) Complete secure payment via UPI, card, or net banking, 5) Receive instant receipt. All donations are routed through verified partner organizations and are tax-deductible. You can track your contributions in "My Contributions" section.'
    },
    {
      keywords: ['volunteer', 'become volunteer', 'volunteer registration', 'help people', 'volunteer signup'],
      answer: 'To become a volunteer: 1) Click "Become a Volunteer" from your dashboard, 2) Fill out the volunteer registration form with your skills and expertise, 3) Select your availability (weekdays, weekends, anytime), 4) Choose support mode (in-person, online, or both), 5) Submit for approval. Once approved, you\'ll receive notifications about volunteer opportunities in your area.'
    },
    {
      keywords: ['safety', 'safety tips', 'preparedness', 'disaster preparedness', 'how to prepare', 'what to do'],
      answer: 'Key safety tips: 1) **Floods**: Move to higher ground, avoid walking in moving water, keep emergency kit ready. 2) **Earthquakes**: Drop, cover, and hold on during shaking, stay away from windows. 3) **Cyclones**: Secure loose objects, stock food/water, stay indoors. 4) **Fires**: Create defensible space, have evacuation routes, keep documents safe. 5) **General**: Maintain emergency contacts, learn first aid, keep flashlights and batteries ready.'
    },
    {
      keywords: ['contact', 'support', 'help', 'customer service', 'phone', 'email', 'emergency contact'],
      answer: 'Contact us via: 1) **Emergency Hotline**: 1800-010-5621 (24/7 toll-free), 2) **Email**: help@alertaid.in (response within 2 hours), 3) **In-app**: Use this chatbot for quick answers, 4) **Location**: New Delhi, India - National Disaster Management Authority Coordination Center. For urgent emergencies, always call local authorities (112) first.'
    }
  ];

  function getBotReply(message) {
    try {
      const normalized = message.toLowerCase().trim();
      
      if (/^(hi|hello|hey|greetings|good morning|good afternoon|good evening)/.test(normalized)) {
        return 'Hello! I\'m the AlertAid Assistant. I can help you with reporting emergencies, tracking reports, donations, volunteering, safety tips, and more. What would you like to know?';
      }
      
      if (/^(thanks|thank you|thank|appreciate)/.test(normalized)) {
        return 'You\'re welcome! If you have any other questions, feel free to ask. Stay safe!';
      }
      
      if (/^(bye|goodbye|see you|farewell)/.test(normalized)) {
        return 'Goodbye! Stay safe and remember: for urgent emergencies, call 112 or 1800-010-5621.';
      }
      
      let bestMatch = null;
      let maxMatches = 0;
      
      faqAnswers.forEach(entry => {
        const matches = entry.keywords.filter(keyword => normalized.includes(keyword)).length;
        if (matches > maxMatches) {
          maxMatches = matches;
          bestMatch = entry;
        }
      });
      
      if (bestMatch && maxMatches > 0) {
        return bestMatch.answer;
      }
      
      if (normalized.includes('emergency') || normalized.includes('urgent') || normalized.includes('help now')) {
        return 'For immediate emergencies, please call 112 (national emergency number) or our hotline 1800-010-5621. You can also submit an emergency report from your dashboard. Stay calm and follow safety protocols.';
      }
      
      return 'I understand you\'re looking for information. I can help with:\n\n• Reporting emergencies and disasters\n• Tracking report status\n• Donations and campaigns\n• Volunteer registration\n• Safety tips and preparedness\n• Account management\n• Contact information\n\nTry asking a specific question, or use the quick action buttons below!';
    } catch (e) {
      console.error('AlertAid Chatbot: Error in getBotReply:', e);
      return 'I apologize, but I encountered an error. Please try asking your question again.';
    }
  }

  function createWidget() {
    try {
      console.log('AlertAid Chatbot: createWidget called');
      
      // Check if already exists
      let widget = document.getElementById('alertaid-chatbot');
      if (widget) {
        console.log('AlertAid Chatbot: Widget already exists, ensuring visibility');
        // Force visibility
        widget.style.cssText = 'position: fixed !important; bottom: 24px !important; right: 24px !important; z-index: 2147483647 !important; display: flex !important; visibility: visible !important; opacity: 1 !important; pointer-events: auto !important; flex-direction: column-reverse !important; align-items: flex-end !important; gap: 14px !important;';
        return widget;
      }

      // Wait for body if needed
      if (!document.body) {
        console.log('AlertAid Chatbot: Body not ready, retrying in 50ms');
        setTimeout(createWidget, 50);
        return null;
      }

      console.log('AlertAid Chatbot: Creating new widget...');

      // Create widget
      widget = document.createElement('div');
      widget.id = 'alertaid-chatbot';
      widget.className = 'chatbot-widget';
      widget.setAttribute('aria-live', 'polite');
      widget.style.cssText = 'position: fixed !important; bottom: 24px !important; right: 24px !important; z-index: 2147483647 !important; display: flex !important; visibility: visible !important; opacity: 1 !important; pointer-events: auto !important; flex-direction: column-reverse !important; align-items: flex-end !important; gap: 14px !important; font-family: Inter, -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif !important;';
      
      // Create button with emoji icon (no Font Awesome dependency)
      const buttonHTML = `
        <button id="chatbotToggleBtn" style="width: 64px !important; height: 64px !important; border-radius: 50% !important; border: none !important; cursor: pointer !important; background: linear-gradient(135deg, #ff512f, #ff6f00) !important; box-shadow: 0 18px 35px rgba(255, 81, 47, 0.35) !important; display: flex !important; align-items: center !important; justify-content: center !important; position: relative !important; z-index: 2147483647 !important; pointer-events: auto !important; transition: transform 0.3s ease, box-shadow 0.3s ease !important;">
          <span style="width: 34px; height: 34px; border-radius: 50%; background: rgba(255,255,255,0.18); border: 1px solid rgba(255,255,255,0.35); display: flex; align-items: center; justify-content: center; font-size: 1.5rem; color: #fff; line-height: 1;">💬</span>
        </button>
      `;
      
      // Create panel
      const panelHTML = `
        <div id="alertaidChatbotPanel" style="width: clamp(320px, 28vw, 360px); max-height: 520px; background: #ffffff; color: #111827; border-radius: 22px; box-shadow: 0 30px 70px rgba(15, 23, 42, 0.25); overflow: hidden; transform: translateY(20px); opacity: 0; pointer-events: none; transition: all 0.3s ease; margin: 0 0 14px 0 !important;">
          <div style="background: linear-gradient(120deg, #134E5E, #71B280); color: #fff; padding: 18px 20px; display: flex; justify-content: space-between; align-items: center;">
            <div>
              <strong>AlertAid Assistant</strong>
              <div style="font-size:0.85rem;opacity:0.8;">Ask disaster or portal questions anytime.</div>
            </div>
            <button type="button" id="chatbotCloseBtn" style="background: none; border: none; color: inherit; font-size: 1.3rem; cursor: pointer; padding: 0; width: 30px; height: 30px; display: flex; align-items: center; justify-content: center;">&times;</button>
          </div>
          <div id="chatbotMessages" style="padding: 18px; height: 260px; overflow-y: auto; background: #f7f8fc; display: flex; flex-direction: column; gap: 12px;"></div>
          <div style="padding: 12px 18px; display: flex; flex-wrap: wrap; gap: 8px; background: #fff; border-top: 1px solid #edf0f7;">
            ${quickPrompts.map(p => `<button type="button" class="quick-btn" data-question="${p.question.replace(/"/g, '&quot;')}" style="border: none; border-radius: 999px; padding: 6px 14px; font-size: 0.8rem; cursor: pointer; background: rgba(19,78,94,0.08); color: #134E5E; transition: background 0.2s ease;">${p.label}</button>`).join('')}
          </div>
          <form id="chatbotForm" style="display: flex; align-items: center; padding: 12px 18px 18px; gap: 12px; background: #fff; border-top: 1px solid #edf0f7;">
            <input type="text" id="chatbotInput" placeholder="Ask a question..." autocomplete="off" style="flex: 1; border: 1px solid #dbe3f0; border-radius: 30px; padding: 10px 16px; font-size: 0.95rem; outline: none;">
            <button type="submit" style="border: none; background: linear-gradient(135deg, #ff512f, #ff6f00); color: #fff; border-radius: 50%; width: 44px; height: 44px; cursor: pointer; display: flex; align-items: center; justify-content: center; font-size: 1rem; box-shadow: 0 12px 24px rgba(255,81,47,0.35);">✈</button>
          </form>
        </div>
      `;
      
      widget.innerHTML = buttonHTML + panelHTML;

      // Append to body
      document.body.appendChild(widget);
      console.log('AlertAid Chatbot: Widget appended to body');
      
      // Wire up functionality
      const toggleBtn = document.getElementById('chatbotToggleBtn');
      const panel = document.getElementById('alertaidChatbotPanel');
      const closeBtn = document.getElementById('chatbotCloseBtn');
      const messages = document.getElementById('chatbotMessages');
      const form = document.getElementById('chatbotForm');
      const input = document.getElementById('chatbotInput');
      const quickBtns = widget.querySelectorAll('.quick-btn');

      if (!toggleBtn || !panel || !messages || !form) {
        console.error('AlertAid Chatbot: Missing required elements', { toggleBtn, panel, messages, form });
        return widget;
      }

      function appendMessage(text, type) {
        try {
          const bubble = document.createElement('div');
          bubble.className = `chatbot-message ${type}`;
          bubble.style.cssText = `padding: 12px 16px; border-radius: 16px; max-width: 85%; font-size: 0.95rem; line-height: 1.45; box-shadow: 0 10px 25px rgba(15, 23, 42, 0.08); white-space: pre-line; word-wrap: break-word; ${type === 'user' ? 'align-self: flex-end; background: #134E5E; color: #fff;' : 'align-self: flex-start; background: #fff;'}`;
          bubble.textContent = text;
          messages.appendChild(bubble);
          messages.scrollTop = messages.scrollHeight;
        } catch (e) {
          console.error('AlertAid Chatbot: Error appending message:', e);
        }
      }

      function togglePanel(forceOpen) {
        try {
          const isOpen = panel.classList.contains('open');
          const shouldOpen = typeof forceOpen === 'boolean' ? forceOpen : !isOpen;
          panel.classList.toggle('open', shouldOpen);
          if (shouldOpen) {
            panel.style.transform = 'translateY(0)';
            panel.style.opacity = '1';
            panel.style.pointerEvents = 'auto';
            if (input) input.focus();
          } else {
            panel.style.transform = 'translateY(20px)';
            panel.style.opacity = '0';
            panel.style.pointerEvents = 'none';
          }
          if (toggleBtn) toggleBtn.setAttribute('aria-expanded', shouldOpen ? 'true' : 'false');
        } catch (e) {
          console.error('AlertAid Chatbot: Error toggling panel:', e);
        }
      }

      function handleUserMessage(text) {
        try {
          if (!text) return;
          appendMessage(text, 'user');
          
          const typingIndicator = document.createElement('div');
          typingIndicator.className = 'chatbot-message bot typing-indicator';
          typingIndicator.style.cssText = 'display: flex; align-items: center; gap: 6px; padding: 12px 16px; align-self: flex-start;';
          typingIndicator.innerHTML = '<span style="width: 8px; height: 8px; border-radius: 50%; background: #134E5E; animation: typing 1.4s infinite;"></span><span style="width: 8px; height: 8px; border-radius: 50%; background: #134E5E; animation: typing 1.4s infinite; animation-delay: 0.2s;"></span><span style="width: 8px; height: 8px; border-radius: 50%; background: #134E5E; animation: typing 1.4s infinite; animation-delay: 0.4s;"></span>';
          messages.appendChild(typingIndicator);
          messages.scrollTop = messages.scrollHeight;
          
          setTimeout(() => {
            try {
              typingIndicator.remove();
              const reply = getBotReply(text);
              appendMessage(reply, 'bot');
            } catch (e) {
              console.error('AlertAid Chatbot: Error in handleUserMessage timeout:', e);
            }
          }, 600 + Math.random() * 400);
        } catch (e) {
          console.error('AlertAid Chatbot: Error in handleUserMessage:', e);
        }
      }

      // Event listeners with error handling
      try {
        toggleBtn.onclick = function(e) {
          e.preventDefault();
          e.stopPropagation();
          togglePanel();
        };
        console.log('AlertAid Chatbot: Toggle button wired up');
      } catch (e) {
        console.error('AlertAid Chatbot: Error wiring toggle button:', e);
      }

      if (closeBtn) {
        try {
          closeBtn.onclick = function(e) {
            e.preventDefault();
            e.stopPropagation();
            togglePanel(false);
          };
        } catch (e) {
          console.error('AlertAid Chatbot: Error wiring close button:', e);
        }
      }

      try {
        form.onsubmit = function(e) {
          e.preventDefault();
          const value = input.value.trim();
          if (value) {
            handleUserMessage(value);
            input.value = '';
          }
        };
        console.log('AlertAid Chatbot: Form wired up');
      } catch (e) {
        console.error('AlertAid Chatbot: Error wiring form:', e);
      }

      try {
        quickBtns.forEach(btn => {
          btn.onclick = function(e) {
            e.preventDefault();
            togglePanel(true);
            handleUserMessage(btn.dataset.question);
          };
        });
        console.log('AlertAid Chatbot: Quick buttons wired up');
      } catch (e) {
        console.error('AlertAid Chatbot: Error wiring quick buttons:', e);
      }

      // Welcome message
      setTimeout(() => {
        try {
          appendMessage('👋 Hello! I\'m the AlertAid Assistant. I\'m here to help you with:\n\n• Reporting emergencies and disasters\n• Tracking your reports\n• Donations and relief campaigns\n• Volunteer opportunities\n• Safety tips and preparedness\n• Account management\n\nAsk me anything or use the quick action buttons below!', 'bot');
        } catch (e) {
          console.error('AlertAid Chatbot: Error showing welcome message:', e);
        }
      }, 300);

      console.log('AlertAid Chatbot: Widget created successfully');
      return widget;
    } catch (error) {
      console.error('AlertAid Chatbot: CRITICAL ERROR creating widget:', error);
      console.error('AlertAid Chatbot: Stack trace:', error.stack);
      return null;
    }
  }

  // CRITICAL: Create widget IMMEDIATELY, synchronously if possible
  // This must happen before any page redirects from dashboard-guard.js or role-router.js
  function initImmediate() {
    try {
      // Try to create widget RIGHT NOW, even if body doesn't exist yet
      if (document.body) {
        console.log('AlertAid Chatbot: Body exists, creating widget IMMEDIATELY');
        const widget = createWidget();
        if (widget) {
          console.log('AlertAid Chatbot: Widget created successfully on immediate init');
        }
      } else {
        // Body doesn't exist - create it as soon as possible
        console.log('AlertAid Chatbot: Body not ready, will create when ready');
        
        // Use multiple strategies to catch body creation
        let retryCount = 0;
        const maxRetries = 100; // Prevent infinite loop
        
        function tryCreate() {
          retryCount++;
          if (document.body) {
            const widget = createWidget();
            if (widget) {
              console.log('AlertAid Chatbot: Widget created on retry', retryCount);
            }
          } else if (retryCount < maxRetries) {
            setTimeout(tryCreate, 10);
          } else {
            console.error('AlertAid Chatbot: Max retries reached, body still not available');
          }
        }
        tryCreate();
        
        // Also listen for DOMContentLoaded
        if (document.addEventListener) {
          document.addEventListener('DOMContentLoaded', function() {
            if (!document.getElementById('alertaid-chatbot') && document.body) {
              createWidget();
            }
          }, { once: true });
        }
      }
    } catch (e) {
      console.error('AlertAid Chatbot: CRITICAL ERROR in initImmediate():', e);
      console.error('AlertAid Chatbot: Stack:', e.stack);
    }
  }

  // EXECUTE IMMEDIATELY - This runs as soon as script loads
  // This is critical - must execute before dashboard-guard.js redirects
  console.log('AlertAid Chatbot: Executing immediate initialization...');
  try {
    initImmediate();
  } catch (e) {
    console.error('AlertAid Chatbot: FATAL ERROR in immediate execution:', e);
  }
  
  // Backup initialization on window load
  if (window.addEventListener) {
    window.addEventListener('load', function() {
      console.log('AlertAid Chatbot: Window load event fired');
      try {
        if (!document.getElementById('alertaid-chatbot')) {
          console.log('AlertAid Chatbot: Widget not found on load, creating...');
          createWidget();
        } else {
          console.log('AlertAid Chatbot: Widget already exists on load');
          // Force visibility
          const widget = document.getElementById('alertaid-chatbot');
          if (widget) {
            widget.style.cssText = 'position: fixed !important; bottom: 24px !important; right: 24px !important; z-index: 2147483647 !important; display: flex !important; visibility: visible !important; opacity: 1 !important; pointer-events: auto !important; flex-direction: column-reverse !important; align-items: flex-end !important; gap: 14px !important;';
          }
        }
      } catch (e) {
        console.error('AlertAid Chatbot: Error in load handler:', e);
      }
    }, { once: true });
  }
  
  // Final safety net: check after 1 second
  setTimeout(function() {
    try {
      const widget = document.getElementById('alertaid-chatbot');
      if (!widget && document.body) {
        console.log('AlertAid Chatbot: Final safety check - widget missing, creating...');
        createWidget();
      } else if (widget) {
        console.log('AlertAid Chatbot: Final safety check - widget exists');
        // Force visibility
        widget.style.cssText = 'position: fixed !important; bottom: 24px !important; right: 24px !important; z-index: 2147483647 !important; display: flex !important; visibility: visible !important; opacity: 1 !important; pointer-events: auto !important; flex-direction: column-reverse !important; align-items: flex-end !important; gap: 14px !important;';
      }
    } catch (e) {
      console.error('AlertAid Chatbot: Error in final safety check:', e);
    }
  }, 1000);

  // MutationObserver to detect if widget is removed and recreate it
  if (typeof MutationObserver !== 'undefined' && document.body) {
    try {
      const observer = new MutationObserver(function(mutations) {
        const widget = document.getElementById('alertaid-chatbot');
        if (!widget && document.body) {
          console.log('AlertAid Chatbot: Widget was removed! Recreating...');
          createWidget();
        } else if (widget) {
          // Ensure it's always visible
          if (widget.style.display === 'none' || widget.style.visibility === 'hidden') {
            console.log('AlertAid Chatbot: Widget was hidden! Making visible...');
            widget.style.cssText = 'position: fixed !important; bottom: 24px !important; right: 24px !important; z-index: 2147483647 !important; display: flex !important; visibility: visible !important; opacity: 1 !important; pointer-events: auto !important; flex-direction: column-reverse !important; align-items: flex-end !important; gap: 14px !important;';
          }
        }
      });
      
      // Start observing when body is ready
      function startObserving() {
        if (document.body) {
          observer.observe(document.body, {
            childList: true,
            subtree: true,
            attributes: true,
            attributeFilter: ['style', 'class']
          });
          console.log('AlertAid Chatbot: MutationObserver started');
        } else {
          setTimeout(startObserving, 100);
        }
      }
      startObserving();
    } catch (e) {
      console.error('AlertAid Chatbot: Error setting up MutationObserver:', e);
    }
  }

  console.log('AlertAid Chatbot: Script execution complete');
})();
