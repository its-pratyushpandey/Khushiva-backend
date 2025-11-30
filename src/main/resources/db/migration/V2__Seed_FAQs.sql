-- V2__Seed_FAQs.sql

INSERT INTO faqs (intent, patterns, responses, quick_replies, priority, is_active) VALUES
(
    'greeting',
    '["^(hi|hello|hey|greetings|good morning|good afternoon|good evening).*", ".*\\b(hi|hello|hey)\\b.*"]',
    '["Hello! How can I help you today?", "Hi there! What can I do for you?", "Hey! Welcome! How may I assist you?", "Greetings! How can I be of service?"]',
    '["View pricing", "Get started", "Contact support", "Learn more"]',
    100,
    TRUE
),
(
    'help',
    '["^(help|assist|support|guide).*", ".*\\b(help|assist|support)\\b.*", ".*what can you do.*", ".*how do (i|you).*"]',
    '["I can help you with pricing information, getting started, account questions, and general support. What would you like to know?", "I''m here to assist! I can answer questions about our services, pricing, onboarding, and more. How can I help?"]',
    '["Pricing info", "Getting started", "Account help", "Contact human"]',
    90,
    TRUE
),
(
    'pricing',
    '[".*\\b(price|pricing|cost|fee|plan|subscription)\\b.*", ".*how much.*", ".*what.*cost.*"]',
    '["We offer flexible pricing plans starting at $29/month for individuals and $99/month for teams. Enterprise pricing is available on request.", "Our pricing varies by plan: Starter ($29/mo), Professional ($79/mo), and Enterprise (custom). Would you like more details on any specific plan?"]',
    '["View all plans", "Compare features", "Contact sales", "Start free trial"]',
    85,
    TRUE
),
(
    'onboarding',
    '[".*\\b(start|begin|onboard|setup|get started|sign up)\\b.*", ".*how to start.*", ".*create account.*"]',
    '["Getting started is easy! Click the ''Sign Up'' button, choose your plan, and follow the setup wizard. We''ll guide you through each step.", "To get started: 1) Create an account, 2) Choose your plan, 3) Complete the setup wizard. Need help with any step?"]',
    '["Sign up now", "Watch tutorial", "Read docs", "Talk to sales"]',
    80,
    TRUE
),
(
    'authentication',
    '[".*\\b(login|signin|sign in|log in|password|reset password|forgot password|authenticate)\\b.*"]',
    '["To log in, click the ''Sign In'' button at the top right. Forgot your password? Use the ''Reset Password'' link on the login page.", "Having trouble logging in? Make sure you''re using the correct email and password. You can reset your password if needed."]',
    '["Reset password", "Sign in", "Contact support"]',
    75,
    TRUE
),
(
    'privacy',
    '[".*\\b(privacy|data|security|gdpr|secure|safe|confidential)\\b.*", ".*my data.*", ".*information.*"]',
    '["We take your privacy seriously. All data is encrypted in transit and at rest. We''re GDPR compliant and never sell your data.", "Your data security is our priority. We use industry-standard encryption, regular security audits, and comply with all major privacy regulations."]',
    '["Read privacy policy", "Security features", "GDPR info"]',
    70,
    TRUE
),
(
    'logout',
    '["^(bye|goodbye|exit|quit|logout|log out|sign out).*", ".*\\b(bye|goodbye|logout)\\b.*"]',
    '["Goodbye! If you need anything else, I''ll be here.", "Thanks for chatting! Feel free to return anytime.", "Take care! Don''t hesitate to reach out if you need help."]',
    '[]',
    60,
    TRUE
),
(
    'error_handling',
    '[".*\\b(error|broken|not working|issue|problem|bug|crash)\\b.*", ".*doesn.*t work.*"]',
    '["I''m sorry you''re experiencing issues. Can you provide more details about the problem? Our support team can help troubleshoot.", "That sounds frustrating. Let me connect you with our technical support team who can investigate this issue."]',
    '["Contact support", "Report bug", "View status page"]',
    55,
    TRUE
),
(
    'fallback',
    '[".*"]',
    '["I''m not sure I understand. Could you rephrase that?", "I didn''t quite catch that. Can you ask in a different way?", "Hmm, I''m still learning about that topic. Would you like to speak with a human agent?"]',
    '["Talk to human", "Main menu", "View FAQ"]',
    10,
    TRUE
),
(
    'small_talk',
    '[".*\\b(how are you|what.*your name|who are you|what are you)\\b.*"]',
    '["I''m doing great, thanks for asking! I''m an AI assistant here to help you.", "I''m your friendly AI assistant! I''m here to make your experience better. How can I help you today?"]',
    '["What can you do?", "Get help", "Learn more"]',
    50,
    TRUE
),
(
    'escalate',
    '[".*\\b(human|agent|representative|person|someone|talk to|speak to)\\b.*", ".*transfer.*", ".*real person.*"]',
    '["I''ll connect you with a human agent right away. Please hold for a moment.", "Let me transfer you to our support team. They''ll be with you shortly."]',
    '["Wait for agent", "Leave message", "Call us"]',
    95,
    TRUE
),
(
    'features',
    '[".*\\b(feature|capability|function|what can|abilities)\\b.*"]',
    '["I can help with account management, answer pricing questions, guide you through setup, troubleshoot issues, and connect you with our team. What would you like to explore?", "Our platform offers real-time analytics, team collaboration tools, automated workflows, and 24/7 support. Which feature interests you?"]',
    '["View all features", "Watch demo", "Start trial", "Compare plans"]',
    65,
    TRUE
);
